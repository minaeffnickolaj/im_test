package org.infomaximum.entities;

import java.util.Objects;

public class Record {
    private String group;
    private String type;
    private long weight;

    public Record(String group, String type, long weight) {
        this.group = group;
        this.type = type;
        this.weight = weight;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        // объект точно является Record
        Record record = (Record) o;
        return (weight == record.weight) && (Objects.equals(group, record.group) && Objects.equals(type, record.type));
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, type, weight);
    }
}
