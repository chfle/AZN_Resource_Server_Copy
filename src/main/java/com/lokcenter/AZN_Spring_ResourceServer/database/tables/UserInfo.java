package com.lokcenter.AZN_Spring_ResourceServer.database.tables;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


@Entity
@TypeDefs({
        @TypeDef(name = "list-array", typeClass = ListArrayType.class),
        @TypeDef(name = "hstore", typeClass = PostgreSQLHStoreType.class)
})
public class UserInfo {
    public enum Balance {
        SCHULD,
        GUTHABEN
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @Setter
    @Getter
    @JsonBackReference
    private Users users;

    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userinfoId;

    /**
     * A Java Map with Year and available Vacation for each year
     */
    @Type(type = "hstore")
    @Column(
            name = "available_vacation" ,
            columnDefinition = "hstore"
    )
    @Setter
    @Getter
    private Map<String, String> availableVacation = new HashMap<>();

    @Type(type = "hstore")
    @Column(
            name = "set_vacation" ,
            columnDefinition = "hstore"
    )
    @Setter
    @Getter
    private Map<String, String> setVacation = new HashMap<>();

    public Map<String, Map<String, Object>> yearToMap() {
        Map<String, Map<String, Object>> resultMap = new HashMap<>();

        for (Field field: this.getClass().getDeclaredFields()) {
            if (field.getType() == Map.class) {
               try {
                   for (var entry: ((HashMap<String, Object>)field.get(this)).entrySet()) {
                      if (resultMap.containsKey(entry.getKey())) {
                          resultMap.get(entry.getKey()).put(field.getName(), entry.getValue());
                      } else {
                          //  should only add years and not dates
                          if (entry.getKey().chars().allMatch(Character::isDigit)) {
                              resultMap.put(entry.getKey(), new HashMap<>(Map.of(field.getName(), entry.getValue())));
                          }
                      }

                   }
               }catch (Exception exception){
                   exception.printStackTrace();
               }
            }
        }

        return resultMap;
    }
}
