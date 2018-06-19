package Controller;

import Entity.Model;
import Controller.util.JsfUtil;
import Controller.util.PaginationHelper;
import Entity.CarGlass;
import ejb.ModelFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
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

@ManagedBean(name = "modelController")
@SessionScoped
public class ModelController implements Serializable {

    private Model current;
    private LazyDataModel items = null;
    private List<Model> filtered;
        private List<CarGlass> carGlasses;
    @EJB
    private ejb.CarGlassFacade carGlassFacade;
    @EJB
    private ejb.ModelFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public ModelController() {
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
   public List<Model> getFiltered() {
        return filtered;
    }

    public void setFiltered(List<Model> filtered) {
        this.filtered = filtered;
    }
    
      public List<Model> completeModel(String query) {
        List<Model> allModels = ejbFacade.findAll();
        List<Model> filtereModels = new ArrayList<Model>();
        if (query == null || query.isEmpty()) {
            filtereModels = allModels;
        } else {
            for (Model color : allModels) {
                if (color.getName().toLowerCase().contains(query.toLowerCase())) {
                    filtereModels.add(color);
                }
            }
        }
        return filtereModels;
    }

    public Model getSelected() {
        if (current == null) {
            current = new Model();
            selectedItemIndex = -1;
        }
        return current;
    }

    public ModelFacade getFacade() {
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
        return "/model/List";
    }

    public String prepareView() {
        current = (Model) getItems().getRowData();
        carGlasses = carGlassFacade.carGlassByIds( null,null,current.getId(),null,null);
        return "/model/View";
    }

    public String prepareCreate() {
        current = new Model();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ModelCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Model) getItems().getRowData();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ModelUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Model) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ModelDeleted"));
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
    public Model getModel(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Model.class, value = "modelConverter")
    public static class ModelControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ModelController controller = (ModelController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "modelController");
            return controller.getModel(getKey(value));
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
            if (object instanceof Model) {
                Model o = (Model) object;
                return getStringKey(o.getId());
            } else if (object instanceof String) {
                return (String) object;
            }else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Model.class.getName());
            }
        }

    }

}
