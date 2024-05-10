package au.edu.sydney.elec5619.leetstem.service.email;

public interface EmailService {
    void sendSignUpVerificationEmail(String token, String email);

    void sendPasswordResetEmail(String token, String email);

    void sendEmailResetEmail(String token, String email);
}
