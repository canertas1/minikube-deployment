package com.example.definex.taskmanagement.entities;

public enum Role{

    GROUP_MANAGER("GROUP_MANAGER"),

    TEAM_LEADER("TEAM_LEADER"),

    TEAM_MEMBER("TEAM_MEMBER");

    private final String roleType;
    Role(String type) {
        roleType = type;
    }
    public String getType() {
        return roleType;
    }
}
