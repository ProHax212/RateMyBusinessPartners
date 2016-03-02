package seniordesign.ratemybusinesspartners;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * Multiple classes can implement sign in and out.
 * Created by HP on 2/27/2016.
 */

public interface GoogleSignIn {

    void signIn();

    void signOut();

    void revokeAccess();

    void handleSignInResult(GoogleSignInResult result);
}
