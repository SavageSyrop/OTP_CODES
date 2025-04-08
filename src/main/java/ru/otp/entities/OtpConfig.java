package ru.otp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "otp_config")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OtpConfig implements Indexable<String> {
    @Column
    private String configVersion;
    @Column
    private Long otpCodeLenth;
    @Column
    private Long exipesAfterMillis;

    @Override
    public void setIndex(String index) {
        setConfigVersion(index);
    }

    @Override
    public String getIndex() {
        return getConfigVersion();
    }
}
