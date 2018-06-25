/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 *
 * @author Moamenovic
 */
@Entity
public class CashFlowReport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date stamp;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date timeTo;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date timeFrom;

    @OneToMany
    @JoinColumn(name = "CASHFLOWREPORT_ID")
    private List<SalesOrder> salesOrders;
    @OneToMany
    @JoinColumn(name = "CASHFLOWREPORT_ID")
    private List<PurchaseOrder> purchaseOrders;

    public Date getStamp() {
        return stamp;
    }

    public void setStamp(Date stamp) {
        this.stamp = stamp;
    }

    public Date getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Date timeTo) {
        this.timeTo = timeTo;
    }

    public Date getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Date timeFrom) {
        this.timeFrom = timeFrom;
    }

    public List<SalesOrder> getSalesOrders() {
        if (salesOrders == null) {
            salesOrders = new ArrayList<SalesOrder>();
        }
        return salesOrders;
    }

    public void setSalesOrders(List<SalesOrder> salesOrders) {
        this.salesOrders = salesOrders;
    }

    public List<PurchaseOrder> getPurchaseOrders() {
        if (purchaseOrders == null) {
            purchaseOrders = new ArrayList<PurchaseOrder>();
        }
        return purchaseOrders;
    }

    public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CashFlowReport)) {
            return false;
        }
        CashFlowReport other = (CashFlowReport) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.CashFlowReport[ id=" + id + " ]";
    }

}
