import java.util.ArrayList;

public class Question {

    public String customerId;
    public String question;
    public String answer;
    public boolean answered;

    // shared storage (THIS replaces QuestionStore)
    public static ArrayList<Question> questions = new ArrayList<>();

    public Question(String customerId, String question) {
        this.customerId = customerId;
        this.question = question;
        this.answered = false;
    }
}