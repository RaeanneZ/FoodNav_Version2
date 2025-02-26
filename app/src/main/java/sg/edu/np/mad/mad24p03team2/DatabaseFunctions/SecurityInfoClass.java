package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

/**
 * SecurityInfoClass
 * Database SecurityInfo Object class
 */
public class SecurityInfoClass {

    private String email;
    private String question;
    private String answer;

    public SecurityInfoClass(){
        this.email = "";
        this.question = "";
        this.answer = "";
    }
    public SecurityInfoClass(String email, String question, String answer){
        this.email = email;
        this.question = question;
        this.answer = answer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
