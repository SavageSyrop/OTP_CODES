package ru.otp.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;


@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User extends AbstractEntity implements Indexable<Long> {

    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String resetPasswordCode;
    @Column
    private String activationCode;
    @Column
    @ColumnDefault("false")
    private Boolean isBanned;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<Role> roles;

    @Override
    public void setIndex(Long index) {
        setId(index);
    }

    @Override
    public Long getIndex() {
        return getId();
    }
}
