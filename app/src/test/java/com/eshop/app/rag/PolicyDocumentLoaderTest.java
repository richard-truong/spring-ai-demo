package com.eshop.app.rag;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyDocumentLoaderTest {

    private final PolicyDocumentLoader loader = new PolicyDocumentLoader();

    @Test
    void loadsProductsSectionsAndClauses() {
        List<PolicyNode> nodes = loader.load();

        List<PolicyNode> products = nodes.stream().filter(n -> n.level() == 1).toList();
        assertThat(products).hasSize(4);
        assertThat(products).extracting(PolicyNode::productName)
            .containsExactly("Espresso", "Cappuccino", "Croissant", "Green Tea");

        List<PolicyNode> sections = nodes.stream().filter(n -> n.level() == 2).toList();
        assertThat(sections).hasSize(8);

        List<PolicyNode> clauses = nodes.stream().filter(n -> n.level() == 3).toList();
        assertThat(clauses).isNotEmpty();
    }

    @Test
    void treeRelationshipsLinkChildrenToParents() {
        List<PolicyNode> nodes = loader.load();
        Map<String, PolicyNode> byId = nodes.stream().collect(Collectors.toMap(PolicyNode::id, n -> n));

        for (PolicyNode node : nodes) {
            if (node.level() == 1) {
                assertThat(node.parentId()).isNull();
            } else {
                assertThat(node.parentId()).isNotNull();
                assertThat(byId).containsKey(node.parentId());
                assertThat(byId.get(node.parentId()).level()).isEqualTo(node.level() - 1);
            }
        }
    }

    @Test
    void sectionsAreCanonicalRefundAndWarranty() {
        List<PolicyNode> sections = loader.load().stream().filter(n -> n.level() == 2).toList();

        assertThat(sections).extracting(PolicyNode::section)
            .containsOnly("REFUND", "WARRANTY");
    }

    @Test
    void clausesCarryProductContextInSearchableText() {
        List<PolicyNode> clauses = loader.load().stream().filter(n -> n.level() == 3).toList();

        assertThat(clauses).allSatisfy(clause -> {
            assertThat(clause.searchableText()).startsWith(clause.productName());
            assertThat(clause.searchableText()).contains(clause.content());
        });
    }

}
