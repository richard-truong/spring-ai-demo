# Hướng dẫn tạo Project / Issue / Sprint trên Jira Cloud bằng API

Tài liệu này ghi lại **toàn bộ quy trình đã chạy thật** để tạo 5 project (HRPM, CAB, RC, BHM, CRP).
Mọi endpoint, tên field và mã lỗi dưới đây đều đã được kiểm chứng, không phải suy đoán.

---

## 0. Chuẩn bị

Token nằm trong `core/.env`:

```
PAT=ATATT3xFfGF0...
```

Token là **Atlassian Cloud API token**, dùng được cho **mọi site** mà tài khoản có quyền —
không phải token riêng của từng site.

> ⚠️ Token thật dài **192 ký tự**. Lỗi copy-paste dư 1 ký tự cuối là nguyên nhân hỏng auth
> phổ biến nhất, và Jira trả về **cùng một thông báo 401 chung chung**, không phân biệt được
> token sai hay email sai. Nghi ngờ thì đếm ký tự trước tiên.

### Xác thực: Basic, KHÔNG phải Bearer

```bash
curl -u "<email>:<token>" https://<site>.atlassian.net/rest/api/3/myself
```

- Email phải là email tài khoản Atlassian, **không phải** email trong git config.
- `Authorization: Bearer <token>` **không hoạt động** — Jira Cloud chỉ nhận JWT ở đó và trả
  `403 {"error": "Failed to parse Connect Session Auth Token"}`.

### Biến dùng chung cho mọi ví dụ dưới

```bash
PAT=$(sed -n 's/^PAT=//p' core/.env | tr -d '\r\n')
EMAIL="binhtt.work@gmail.com"
SITE="https://<site>.atlassian.net"
```

---

## 1. Tạo project

```bash
curl -u "$EMAIL:$PAT" -H 'Content-Type: application/json' \
  -X POST "$SITE/rest/simplified/1.0/project" \
  -d '{"name":"Car Rental Platform","key":"CRP",
       "templateKey":"com.pyxis.greenhopper.jira:gh-simplified-agility-scrum"}'
```

Trả về: `{"returnUrl":"/browse/CRP?initial=true","projectId":10003,"projectKey":"CRP",...}`

### 🔴 Chọn template là quyết định KHÔNG đổi lại được

| `templateKey` | Có sprint? | Ghi chú |
|---|---|---|
| `...:gh-simplified-agility-scrum` | ✅ Có backlog + sprint | **Luôn dùng cái này** |
| `...:gh-simplified-agility-kanban` | ❌ Không, vĩnh viễn | Board Kanban, không convert được |

Chọn nhầm Kanban thì API sẽ trả `400 "The board does not support sprints"` và
`"Backlogs are not supported on this board"`, field `customfield_10020` (Sprint) **không tồn tại**
trong createmeta, và không có toggle nào để bật lại. Cách duy nhất là **tạo project mới**.

### Lưu ý về quyền

- Cần `ADMINISTER` (Administer Jira) **chỉ khi** tạo project **company-managed** qua
  `POST /rest/api/3/project` → nếu thiếu sẽ báo
  `403 "You must have global administrator rights in order to modify projects."`
- Tạo project **team-managed** qua `/rest/simplified/1.0/project` **không cần** `ADMINISTER`.
  Đã tạo thành công trên các site chỉ có `ADMINISTER=False`, `ADMINISTER_PROJECTS=False`.
- Tên project **không được trùng** với project đang tồn tại (kể cả đang nằm trong thùng rác):
  `400 {"errors":{"projectName":"A project with that name already exists."}}`

---

## 2. Lấy board id và kiểm tra sprint

```bash
curl -u "$EMAIL:$PAT" "$SITE/rest/agile/1.0/board?projectKeyOrId=CRP"
```

Lấy `values[0].id` → đó là `BOARD_ID`.

Kiểm tra board có hỗ trợ sprint không:

```bash
curl -u "$EMAIL:$PAT" "$SITE/rest/agile/1.0/board/$BOARD_ID/sprint"    # phải trả 200
curl -u "$EMAIL:$PAT" "$SITE/rest/agile/1.0/board/$BOARD_ID/backlog"   # phải trả 200
```

---

## 3. Sprint: đổi tên, đặt ngày, khởi động

Jira **tự tạo sẵn một sprint** tên `<KEY> Sprint 1` khi tạo project. Không cần tạo mới —
đổi tên và đặt ngày cho chính nó:

```bash
curl -u "$EMAIL:$PAT" -H 'Content-Type: application/json' \
  -X POST "$SITE/rest/agile/1.0/sprint/$SPRINT_ID" \
  -d '{"name":"Sprint 0",
       "startDate":"2026-09-07T09:00:00.000+07:00",
       "endDate":"2026-09-19T18:00:00.000+07:00"}'
```

Khởi động sprint (chuyển `future` → `active`):

```bash
curl -u "$EMAIL:$PAT" -H 'Content-Type: application/json' \
  -X POST "$SITE/rest/agile/1.0/sprint/$SPRINT_ID" -d '{"state":"active"}'
```

> ❌ **`POST /rest/agile/1.0/sprint/{id}/state` KHÔNG tồn tại** → 404. Phải dùng
> `POST /rest/agile/1.0/sprint/{id}` với body `{"state":"active"}`.

Thứ tự nên làm: đổi tên + ngày → tạo issue → gán vào sprint → **start sau cùng**.

---

## 4. 🔴 Lấy issue type ID — BẮT BUỘC, KHÔNG ĐƯỢC BỎ QUA

**ID của Epic/Task/Story/Subtask là riêng theo từng project.** Dùng ID của project này cho
project khác sẽ lỗi `400 {"errors":{"issuetype":"Specify a valid issue type"}}`.

```bash
curl -u "$EMAIL:$PAT" "$SITE/rest/api/3/issue/createmeta/CRP/issuetypes"
```

Ví dụ thực tế **trên cùng một site**:

| Project | Epic | Task | Story | Subtask |
|---|---|---|---|---|
| SCRUM | 10001 | 10003 | 10004 | 10002 |
| CRP (tạo sau) | 10007 | 10009 | 10010 | 10008 |

Lấy ID xong mới sang bước 5.

---

## 5. Tạo Epic

```bash
curl -u "$EMAIL:$PAT" -H 'Content-Type: application/json' \
  -X POST "$SITE/rest/api/3/issue" \
  -d '{"fields":{"project":{"key":"CRP"},"issuetype":{"id":"<EPIC_ID>"},
       "summary":"Authentication & Authorization","description":<ADF>}}'
```

Epic **không** cần `parent`, và **không** nên gán story points.

### Định dạng description — Atlassian Document Format (ADF)

Jira Cloud v3 **không nhận plain text** ở field `description`. Phải là ADF:

```json
{"type":"doc","version":1,"content":[
  {"type":"paragraph","content":[{"type":"text","text":"Một đoạn văn."}]},
  {"type":"bulletList","content":[
    {"type":"listItem","content":[
      {"type":"paragraph","content":[{"type":"text","text":"Một gạch đầu dòng."}]}]}]}
]}
```

Helper Python dùng lại được:

```python
def para(t):  return {"type":"paragraph","content":[{"type":"text","text":t}]}
def bullet(xs):
    return {"type":"bulletList","content":[
        {"type":"listItem","content":[para(x)]} for x in xs]}
def doc(*blocks): return {"type":"doc","version":1,"content":list(blocks)}
def story(role, goal, benefit, acs):
    return doc(para(f"As a {role}, I want {goal}, so that {benefit}."),
               para("Acceptance Criteria"),
               bullet([f"Given {c}." for c in acs]))
def task(items, dod):
    return doc(bullet(items), para(f"Definition of Done: {dod}"))
```

---

## 6. Tạo Task và Story

Khác nhau **chỉ ở `issuetype.id`**. Cả hai đều gắn vào Epic bằng field `parent`:

```bash
curl -u "$EMAIL:$PAT" -H 'Content-Type: application/json' \
  -X POST "$SITE/rest/api/3/issue" \
  -d '{"fields":{"project":{"key":"CRP"},"issuetype":{"id":"<STORY_ID>"},
       "summary":"Login for All Roles","description":<ADF>,
       "parent":{"key":"CRP-2"},"customfield_10016":5}}'
```

- **`parent`** = key của Epic. Project team-managed dùng `parent`, **không** dùng "Epic Link".
- **`customfield_10016`** = Story point estimate.
- **`customfield_10020`** = Sprint (thường gán qua API sprint ở bước 8, không set trực tiếp).

---

## 7. Tạo Subtask

**Cùng một cơ chế**: `parent` trỏ tới issue cha, `issuetype.id` là ID của `Subtask`.

```bash
curl -u "$EMAIL:$PAT" -H 'Content-Type: application/json' \
  -X POST "$SITE/rest/api/3/issue" \
  -d '{"fields":{"project":{"key":"CRP"},"issuetype":{"id":"<SUBTASK_ID>"},
       "parent":{"key":"CRP-5"},"summary":"Viết migration cho bảng users"}}'
```

Đã kiểm chứng: tạo được, `parent` trỏ đúng issue cha, `issuetype.subtask = true`.

Subtask **không** gắn trực tiếp vào Epic — nó thuộc issue cha, và issue cha mới thuộc Epic.

Xoá issue (dùng khi test nhầm):

```bash
curl -u "$EMAIL:$PAT" -X DELETE "$SITE/rest/api/3/issue/CRP-25"   # 204, sau đó GET trả 404
```

---

## 8. Gán issue vào sprint

```bash
curl -u "$EMAIL:$PAT" -H 'Content-Type: application/json' \
  -X POST "$SITE/rest/agile/1.0/sprint/$SPRINT_ID/issue" \
  -d '{"issues":["CRP-1","CRP-2","CRP-3","CRP-5"]}'
```

Trả `204` là thành công (không có body). Gọi được cho cả sprint `future` lẫn `active` —
nên gom 1 lần với danh sách key để đỡ nhiều request.

---

## 9. Verify sau khi tạo

```bash
# Đếm tổng
curl -u "$EMAIL:$PAT" -H 'Content-Type: application/json' \
  -X POST "$SITE/rest/api/3/search/approximate-count" -d '{"jql":"project = CRP"}'

# Liệt kê kèm type, parent, points
curl -u "$EMAIL:$PAT" \
  "$SITE/rest/api/3/search/jql?jql=project%3DCRP&maxResults=100&fields=summary,issuetype,parent,customfield_10016"

# Sprint hiện tại
curl -u "$EMAIL:$PAT" "$SITE/rest/agile/1.0/sprint/$SPRINT_ID"

# Issue trong sprint
curl -u "$EMAIL:$PAT" "$SITE/rest/agile/1.0/sprint/$SPRINT_ID/issue?maxResults=100"

# Epic trên board
curl -u "$EMAIL:$PAT" "$SITE/rest/agile/1.0/board/$BOARD_ID/epic"
```

> ⚠️ `search/approximate-count` **không cho phép JQL không giới hạn** — phải có điều kiện lọc
> như `project = X`. Query trần sẽ trả `400 "Unbounded JQL queries are not allowed here."`

---

## 10. Bảng lỗi thường gặp

| Lỗi | Nguyên nhân | Cách sửa |
|---|---|---|
| `400 Specify a valid issue type` | Dùng ID issue type của project khác | Gọi lại `createmeta/{KEY}/issuetypes` |
| `400 A project with that name already exists` | Tên hoặc key còn bị giữ | Đổi tên project cũ, hoặc dùng key khác |
| `400 The board does not support sprints` | Project tạo bằng template Kanban | Không sửa được — phải tạo project mới bằng template scrum |
| `403 You must have global administrator rights` | Thiếu `ADMINISTER` | Dùng `/rest/simplified/1.0/project` (team-managed) |
| `403 You cannot delete this project` | Purge khỏi thùng rác cần `ADMINISTER` | Nhờ Jira admin vào Settings → Projects → Recycle bin |
| `404 No endpoint GET /rest/api/3/spaces` | Jira **không có** khái niệm "space" | "Space" là của Confluence, không phải Jira |
| `404` khi start sprint | Gọi `/sprint/{id}/state` | Dùng `POST /sprint/{id}` với `{"state":"active"}` |
| `403 Failed to parse Connect Session Auth Token` | Dùng Bearer auth | Đổi sang Basic |
| `401 Client must be authenticated` | Token sai **hoặc** email sai — thông báo giống hệt nhau | Đếm ký tự token (192), kiểm tra lại email |

### Xoá project thì KHÔNG giải phóng key

`DELETE /rest/api/3/project/{id}` trả `204` nhưng chỉ là **xoá mềm** vào thùng rác. Key và tên
vẫn bị giữ, tạo lại cùng key sẽ lỗi. Muốn lấy lại key thì phải nhờ admin purge thùng rác —
thao tác đó cần quyền `ADMINISTER`.

Cách vượt rào đã dùng thành công:
- `PUT /rest/api/3/project/{id}` với `{"name":"..."}` → đổi tên để **giải phóng tên**.
- Dùng **key khác** cho project mới.
- `POST /rest/api/3/project/{id}/restore` → hoàn tác xoá mềm.

---

## 11. Script mẫu hoàn chỉnh

Chạy được ngay — chỉ sửa `BASE`, `PROJECT`, `SPRINT_ID` và ID issue type.

```python
import json, re, urllib.request, base64

ENV  = open('core/.env').read()
PAT  = re.search(r'^PAT=(.+)$', ENV, re.M).group(1).strip()
BASE = 'https://<site>.atlassian.net'
AUTH = base64.b64encode(f'binhtt.work@gmail.com:{PAT}'.encode()).decode()

PROJECT, SPRINT_ID = 'CRP', 4
EPIC, TASK, STORY, SUBTASK = '10007', '10009', '10010', '10008'
SP_FIELD = 'customfield_10016'

def call(method, path, body=None):
    req = urllib.request.Request(BASE + path, method=method,
        headers={'Authorization': 'Basic ' + AUTH, 'Content-Type': 'application/json'})
    data = json.dumps(body).encode() if body is not None else None
    try:
        with urllib.request.urlopen(req, data) as r:
            return r.status, json.loads(r.read() or b'{}')
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read() or b'{}')

def para(t):  return {"type":"paragraph","content":[{"type":"text","text":t}]}
def bullet(xs):
    return {"type":"bulletList","content":[
        {"type":"listItem","content":[para(x)]} for x in xs]}
def doc(*blocks): return {"type":"doc","version":1,"content":list(blocks)}

def create(itype, summary, desc, parent=None, points=None):
    fields = {'project': {'key': PROJECT}, 'issuetype': {'id': itype},
              'summary': summary, 'description': desc}
    if parent:  fields['parent'] = {'key': parent}
    if points:  fields[SP_FIELD] = points
    return call('POST', '/rest/api/3/issue', {'fields': fields})

# Epic (không parent, không points)
st, epic = create(EPIC, "Authentication & Authorization",
                  doc(para("Registration, login and role-based access control.")))
epic_key = epic['key']

# Story gắn vào Epic, có points
st, s = create(STORY, "Login for All Roles",
               doc(para("As a user, I want to log in, so that I can reach my features."),
                   para("Acceptance Criteria"),
                   bullet(["valid credentials are submitted, when login succeeds, then a token is issued."])),
               parent=epic_key, points=5)

# Subtask gắn vào Story (không phải vào Epic)
st, sub = create(SUBTASK, "Viết unit test cho login", doc(bullet(["Test happy path"])),
                 parent=s['key'])

# Task gắn vào Epic
st, t = create(TASK, "Setup CI/CD", doc(bullet(["Chạy test trên mọi PR"])),
               parent=epic_key, points=5)

# Gán tất cả vào sprint (bỏ qua Epic nếu không muốn)
keys = [epic_key, s['key'], t['key']]
print(call('POST', f'/rest/agile/1.0/sprint/{SPRINT_ID}/issue', {'issues': keys}))

# Start sprint
print(call('POST', f'/rest/agile/1.0/sprint/{SPRINT_ID}', {'state': 'active'}))
```

---

## 12. Checklist thứ tự thao tác

1. Đếm ký tự PAT = 192, xác thực bằng `/rest/api/3/myself`
2. Tạo project với template **`gh-simplified-agility-scrum`**
3. Lấy `BOARD_ID` từ `/rest/agile/1.0/board?projectKeyOrId=<KEY>`
4. Đổi tên sprint auto-create thành `Sprint 0` + đặt `startDate`/`endDate`
5. **Gọi `createmeta/<KEY>/issuetypes` lấy ID Epic/Task/Story/Subtask**
6. Tạo Epic → lấy về danh sách key
7. Tạo Task/Story với `parent` = epic key + `customfield_10016` = points
8. Tạo Subtask với `parent` = key của Task/Story
9. Gán issue vào sprint: `POST /rest/agile/1.0/sprint/{id}/issue`
10. Start sprint: `POST /rest/agile/1.0/sprint/{id}` với `{"state":"active"}`
11. Verify: đếm issue, đếm theo type, kiểm tra parent + points, kiểm tra sprint
