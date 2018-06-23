package Controller;

import Entity.Partation;
import Controller.util.JsfUtil;
import Controller.util.PaginationHelper;
import Entity.CarGlass;
import ejb.PartationFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean(name = "partationController")
@SessionScoped
public class PartationController implements Serializable {

    private Partation current;
    private LazyDataModel items = null;
    private List<Partation> filtered;
        private List<CarGlass> carGlasses;
    @EJB
    private ejb.CarGlassFacade carGlassFacade;
    @EJB
    private ejb.PartationFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public PartationController() {
        items = new LazyDataModel() {

            @Override

            public List load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {

                List l = getFacade().findRange(first, pageSize, sortField, sortOrder, filters);

                setRowCount(l.size());

                if (l.size() > pageSize) {

                    try {

                        return l.subList(first, first + pageSize);

                    } catch (IndexOutOfBoundsException e) {

                        return l.subList(first, first + (l.size() % pageSize));

                    }

                } else {

                    return l;

                }

            }

        };
    }
   public List<Partation> getFiltered() {
        return filtered;
    }

    public void setFiltered(List<Partation> filtered) {
        this.filtered = filtered;
    }
    
      public List<Partation> completePartation(String query) {
        List<Partation> allPartations = ejbFacade.findAll();
        List<Partation> filterePartations = new ArrayList<Partation>();
        if (query == null || query.isEmpty()) {
            filterePartations = allPartations;
        } else {
            for (Partation color : allPartations) {
                if (color.getName().toLowerCase().contains(query.toLowerCase())) {
                    filterePartations.add(color);
                }
            }
        }
        return filterePartations;
    }

    public Partation getSelected() {
        if (current == null) {
            current = new Partation();
            selectedItemIndex = -1;
        }
        return current;
    }

    public PartationFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        return "/partation/List";
    }

    public String prepareView() {
        current = (Partation) getItems().getRowData();
         carGlasses = carGlassFacade.carGlassByIds( null,null,null,current.getId(),null);
       
        return "/partation/View";
    }

    public String prepareCreate() {
        current = new Partation();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PartationCreated"));
            return prepareCreate();
        } catch(EJBException e){
             JsfUtil.addErrorMessage( ResourceBundle.getBundle("/Bundle").getString("DublicationError"));
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Partation) getItems().getRowData();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PartationUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Partation) getItems().getRowData();
        performDestroy();
        recreatePagination();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PartationDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
//        if (items == null) {
//            items = getPagination().createPageDataModel();
//        }
        return items;
    }


    private void recreatePagination() {
        pagination = null;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }
        public List<CarGlass> getCarGlasses() {
        if(carGlasses == null){
            carGlasses =  new ArrayList<>();
        }
        return carGlasses;
    }

    public void setCarGlasses(List<CarGlass> carGlasses) {
        this.carGlasses = carGlasses;
    }
    public Partation getPartation(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Partation.class, value = "partationConverter")
    public static class PartationControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PartationController controller = (PartationController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "partationController");
            return controller.getPartation(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Partation) {
                Partation o = (Partation) object;
                return getStringKey(o.getId());
            }else if (object instanceof String) {
                return (String) object;
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Partation.class.getName());
            }
        }

    }

}
