package ru.exlmoto.lab;

public class GoToPageForm {
    private String page;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public GoToPageForm(String path) {
        this.path = path;
    }
}
