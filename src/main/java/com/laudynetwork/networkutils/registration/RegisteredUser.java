package com.laudynetwork.networkutils.registration;

import java.util.UUID;

public record RegisteredUser(boolean successfully, UUID token) {
}
