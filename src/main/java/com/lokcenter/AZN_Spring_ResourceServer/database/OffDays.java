package com.lokcenter.AZN_Spring_ResourceServer.database;

import javax.persistence.*;
import java.util.Date;

/**
 * Table for holidays and vacation
 *
 * @version 1.01 2022-06-07
 */
@Entity
@Table(name = "off_days")
public class OffDays {
    /**
     * Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long a_id;

    @Column(nullable = false, name = "date")
    @Temporal(TemporalType.DATE)
    private Date b_date;

    @Column(nullable = false, name = "general_vacation")
    private int c_generalVacation;

    @Column(nullable = false, name = "official_holiday")
    private int d_officialHoliday;

    @Column(nullable = true, name = "comment")
    private String e_comment;

    public OffDays() {

    }

    public Long getId() {
        return a_id;
    }

    public void setId(Long id) {
        this.a_id = id;
    }

    public Date getDate() {
        return b_date;
    }

    public void setDate(Date date) {
        this.b_date = date;
    }

    public int getGeneralVacation() {
        return c_generalVacation;
    }

    public void setGeneralVacation(int generalVacation) {
        this.c_generalVacation = generalVacation;
    }

    public int getOfficialHoliday() {
        return d_officialHoliday;
    }

    public void setOfficialHoliday(int officialHoliday) {
        this.d_officialHoliday = officialHoliday;
    }

    public String getComment() {
        return e_comment;
    }

    public void setComment(String comment) {
        this.e_comment = comment;
    }
}
