package Controller;

import Entity.Position;
import Controller.util.JsfUtil;
import Controller.util.PaginationHelper;
import Entity.CarGlass;
import ejb.PositionFacade;
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

@ManagedBean(name = "positionController")
@SessionScoped
public class PositionController implements Serializable {

    private Position current;
    private LazyDataModel items = null;
    private List<Position> filtered;
    private List<CarGlass> carGlasses;
    @EJB
    private ejb.CarGlassFacade carGlassFacade;
    @EJB
    private ejb.PositionFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public PositionController() {
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

    public List<Position> getFiltered() {
        return filtered;
    }

    public void setFiltered(List<Position> filtered) {
        this.filtered = filtered;
    }

    public List<Position> completePosition(String query) {
        List<Position> allPositions = ejbFacade.findAll();
        List<Position> filterePositions = new ArrayList<Position>();
        if (query == null || query.isEmpty()) {
            filterePositions = allPositions;
        } else {
            for (Position color : allPositions) {
                if (color.getName().toLowerCase().contains(query.toLowerCase())) {
                    filterePositions.add(color);
                }
            }
        }
        return filterePositions;
    }

    public Position getSelected() {
        if (current == null) {
            current = new Position();
            selectedItemIndex = -1;
        }
        return current;
    }

    public PositionFacade getFacade() {
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
        return "/position/List";
    }

    public String prepareView() {
        current = (Position) getItems().getRowData();
        carGlasses = carGlassFacade.carGlassByIds( null,null,null,null,current.getId());
        return "/position/View";
    }

    public String prepareCreate() {
        current = new Position();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PositionCreated"));
            return prepareCreate();
        }  catch(EJBException e){
             JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("DublicationError"));
            return null;
        }catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Position) getItems().getRowData();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PositionUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Position) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PositionDeleted"));
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
        if (carGlasses == null) {
            carGlasses = new ArrayList<>();
        }
        return carGlasses;
    }

    public void setCarGlasses(List<CarGlass> carGlasses) {
        this.carGlasses = carGlasses;
    }

    public Position getPosition(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Position.class, value = "positionConverter")
    public static class PositionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PositionController controller = (PositionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "positionController");
            return controller.getPosition(getKey(value));
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
            if (object instanceof Position) {
                Position o = (Position) object;
                return getStringKey(o.getId());
            } else if (object instanceof String) {
                return (String) object;
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Position.class.getName());
            }
        }

    }

}
