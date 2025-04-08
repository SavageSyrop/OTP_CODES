package ru.otp.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otp.enums.RoleType;


@Entity
@Table(name = "roles")
@NoArgsConstructor
@Getter
@Setter
public class Role extends AbstractEntity implements Indexable<Long> {
    @Enumerated(EnumType.STRING)
    @Column
    private RoleType name;
    @Column(name = "user_id")
    private Long userId;

    @Override
    public void setIndex(Long index) {
        setId(index);
    }

    @Override
    public Long getIndex() {
        return getId();
    }
}