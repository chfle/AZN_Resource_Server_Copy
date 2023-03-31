package com.lokcenter.AZN_Spring_ResourceServer.database.interfaces;

import java.util.UUID;

/**
 * UUID Interface for UUID field classes
 */
public interface IUuidable {
    public void setUuid(UUID uuid);
    public UUID getUuid();
}
