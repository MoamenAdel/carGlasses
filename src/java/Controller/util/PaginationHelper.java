package Controller.util;

import javax.faces.model.DataModel;

public abstract class PaginationHelper {

    private int pageSize;
    private int page;
    private int pages;

    public PaginationHelper(int pageSize) {
        this.pageSize = pageSize;
        // this.page = pageSize / 10;
    }

    public abstract int getItemsCount();

    public abstract DataModel createPageDataModel();

    public int getNumberOfPages() {
        pages = getItemsCount() / pageSize;
        if (getItemsCount() % pageSize > 0) {
            pages++;
        }

        return pages;
    }

    public int getPage() {
        return page;
    }

//   public void setPage(int i){
//       page = i;
//   }
    public int getPageFirstItem() {
        return page * pageSize;
    }

    public int getPageLastItem() {
        int i = getPageFirstItem() + pageSize - 1;
        int count = getItemsCount() - 1;
        if (i > count) {
            i = count;
        }
        if (i < 0) {
            i = 0;
        }

//        int i = getItemsCount() % pageSize;
//        if (i > 0)
//            i++;
        return i;
    }

    public boolean isHasNextPage() {
        return (page + 1) * pageSize + 1 <= getItemsCount();
    }

    public void nextPage() {
        if (isHasNextPage()) {
            page++;
        }
    }

    public boolean isHasPreviousPage() {
        return page > 0;
    }

    public void previousPage() {
        if (isHasPreviousPage()) {
            page--;
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize){
        this.pageSize = pageSize;
    }

    public void setPage(int i) {
        this.page = i;
    }

}
