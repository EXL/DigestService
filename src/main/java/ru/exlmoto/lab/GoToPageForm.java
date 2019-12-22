package ru.exlmoto.lab;

public class GoToPageForm {
    private String page;
    private String path;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public GoToPageForm(String path, String text) {
        this.path = path;
        this.text = text;
    }
}
