public abstract class BaseReminder implements ReminderInterface {
    protected String name;
    protected String date;

    public BaseReminder(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public abstract String getReminderMessage();

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}
