package com.lokcenter.AZN_Spring_ResourceServer.database;


import com.lokcenter.AZN_Spring_ResourceServer.helper.UserDepending;
import javax.persistence.*;

/**
 * Stores user data
 */
@UserDepending
@Entity
@Table(name = "user_data")
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * id from User table
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user_id;

    /**
     * Select if handicapped
     */
    private int handicap;

    /**
     * Job length
     */
    private int job_length;

    /**
     * Positive or negative credit based on enum value
     *
     * @implNote columnDefinition = "ENUM('NEGATIVE', 'POSITIVE') must be set /
     * for mysql
     */
    @Column(nullable = false, columnDefinition = "ENUM('NEGATIVE', 'POSITIVE')", name = "balance_time")
    private Balance a_balanceTime;

    @Column(nullable = false, name = "used_vacationDays")
    private Long b_usedVacationDays;

    @Column(nullable = false, name = "sick_days")
    private int c_sickDays;

    @Column(nullable = false, name = "glaz_days")
    private int d_glazDays;

    @Column(nullable = false, name = "vacation_while_sick")
    private Long e_vacationWhileSick;

    public UserData() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public Balance getBalanceTime() {
        return a_balanceTime;
    }

    public void setBalanceTime(Balance balanceTime) {
        this.a_balanceTime = balanceTime;
    }

    public Long getUsedVacationDays() {
        return b_usedVacationDays;
    }

    public void setUsedVacationDays(Long usedVacationDays) {
        this.b_usedVacationDays = usedVacationDays;
    }

    public int getSickDays() {
        return c_sickDays;
    }

    public void setSickDays(int sickDays) {
        this.c_sickDays = sickDays;
    }

    public int getGlazDays() {
        return d_glazDays;
    }

    public void setGlazDays(int glazDays) {
        this.d_glazDays = glazDays;
    }

    public Long getVacationWhileSick() {
        return e_vacationWhileSick;
    }

    public void setVacationWhileSick(Long vacationWhileSick) {
        this.e_vacationWhileSick = vacationWhileSick;
    }

    public UserData(Long id, User user_id, int handicap, int job_length, Balance a_balanceTime, Long b_usedVacationDays, int c_sickDays, int d_glazDays, Long e_vacationWhileSick) {
        this.id = id;
        this.user_id = user_id;
        this.handicap = handicap;
        this.job_length = job_length;
        this.a_balanceTime = a_balanceTime;
        this.b_usedVacationDays = b_usedVacationDays;
        this.c_sickDays = c_sickDays;
        this.d_glazDays = d_glazDays;
        this.e_vacationWhileSick = e_vacationWhileSick;
    }

    public int getHandicap() {
        return handicap;
    }

    public void setHandicap(int handicap) {
        this.handicap = handicap;
    }

    public double getJob_length() {
        return job_length;
    }

    public void setJob_length(int job_length) {
        this.job_length = job_length;
    }

    public Balance getA_balanceTime() {
        return a_balanceTime;
    }

    public void setA_balanceTime(Balance a_balanceTime) {
        this.a_balanceTime = a_balanceTime;
    }

    public Long getB_usedVacationDays() {
        return b_usedVacationDays;
    }

    public void setB_usedVacationDays(Long b_usedVacationDays) {
        this.b_usedVacationDays = b_usedVacationDays;
    }

    public int getC_sickDays() {
        return c_sickDays;
    }

    public void setC_sickDays(int c_sickDays) {
        this.c_sickDays = c_sickDays;
    }

    public int getD_glazDays() {
        return d_glazDays;
    }

    public void setD_glazDays(int d_glazDays) {
        this.d_glazDays = d_glazDays;
    }

    public Long getE_vacationWhileSick() {
        return e_vacationWhileSick;
    }

    public void setE_vacationWhileSick(Long e_vacationWhileSick) {
        this.e_vacationWhileSick = e_vacationWhileSick;
    }
}
