package com.eshop.core.application.port.out;

import java.time.Instant;

public interface ClockPort {

    Instant now();

}
