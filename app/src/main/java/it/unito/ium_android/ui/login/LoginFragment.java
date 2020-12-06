package it.unito.ium_android.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

import it.unito.ium_android.R;
import it.unito.ium_android.requests.Requests;


public class LoginFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        //setRetainInstance(true);

        MaterialButton btnLogin = root.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> {
            String username = Objects.requireNonNull(((TextInputLayout) root.findViewById(R.id.usernameEditText)).getEditText()).getText().toString();
            String password = Objects.requireNonNull(((TextInputLayout) root.findViewById(R.id.passwordEditText)).getEditText()).getText().toString();
            if (username.equals("") || password.equals("")) {
                Toast.makeText(requireActivity().getBaseContext(), "Username e/o Password sbagliati", Toast.LENGTH_SHORT).show();
            } else {
                Requests post = new Requests(getActivity(), "login");
                try {
                    String data = "username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&action=login";
                    String method = "POST";
                    post.execute(data, Requests.url, method);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's state here
        outState.putString("username", Objects.requireNonNull(((TextInputLayout) getView().findViewById(R.id.usernameEditText)).getEditText()).getText().toString());
        outState.putString("password", Objects.requireNonNull(((TextInputLayout) getView().findViewById(R.id.passwordEditText)).getEditText()).getText().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
            String username = savedInstanceState.getString("username");
            String password = savedInstanceState.getString("password");

            (((TextInputLayout) getView().findViewById(R.id.usernameEditText)).getEditText()).setText(username);
            (((TextInputLayout) getView().findViewById(R.id.passwordEditText)).getEditText()).setText(password);
        }
    }*/

}