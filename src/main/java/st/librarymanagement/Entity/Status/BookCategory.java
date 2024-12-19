package st.librarymanagement.Entity.Status;


public enum BookCategory {
    ScienceFiction("Fiction", "Books that contain imaginary or fictional stories"),
    NonFiction("Non-Fiction", "Books that provide factual information"),
    Science("Science", "Books related to scientific topics"),
    History("History", "Books about historical events"),
    Technology("Technology", "Books about technology and computing"),
    Art("Art", "Books related to art and creativity"),
    Horror("Horror", "Books that are intended to scare or frighten the reader"),
    Fantasy("Fantasy", "Books that contain magical or supernatural elements"),
    Mystery("Mystery", "Books that involve solving a crime or puzzle");

    private final String displayName;
    private final String description;

    BookCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
