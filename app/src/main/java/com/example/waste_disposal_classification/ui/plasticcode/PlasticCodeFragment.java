package com.example.waste_disposal_classification.ui.plasticcode;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.waste_disposal_classification.R;

/**
 * PlasticCode fragment that is used for identifying resin codes
 */

public class PlasticCodeFragment extends Fragment {
    private Button submitBtn;
    private ImageView imageView;
    private PlasticCodeViewModel plasticCodeViewModel;
    private LinearLayout layout;
    private EditText codeInput;
    private TextView textView;
    private int images[]={R.drawable.code_1,R.drawable.code_2,R.drawable.code_3,R.drawable.code_4,R.drawable.code_5,R.drawable.code_6,R.drawable.code_7};
    private int code;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        plasticCodeViewModel =
                new ViewModelProvider(this).get(PlasticCodeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_plastic_code, container, false);
        initUIElements(root);
        return root;
    }

    /**
     * initializes variables and sets onclick events
     * @param root
     */
    private void initUIElements(View root) {
        submitBtn = (Button) root.findViewById(R.id.button_code);
        codeInput = (EditText) root.findViewById(R.id.input_code);
        textView = (TextView) root.findViewById(R.id.recyclable_result_text);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeInput.onEditorAction(EditorInfo.IME_ACTION_DONE);
                imageView = (ImageView) root.findViewById(R.id.images);
                try
                {
                    code = Integer.valueOf(codeInput.getText().toString());
                    if(code == 1 || code == 2 || code == 6){
                        textView.setText("This is Recyclable!");
                    }else if(code == 5 || code == 3 || code == 4 || code == 7){
                        textView.setText("This is NOT Recyclable!");
                    }
                    if(code > 7 || code < 1){
                        throw new IndexOutOfBoundsException();
                    }
                    imageView.setImageResource(images[code-1]);
                }
                catch(IndexOutOfBoundsException e){

                    Toast toast = Toast.makeText(getContext(),"Invalid code: please enter a code that is  between 1 and 7", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 100);
                    toast.show();
                }
                catch (Exception e){
                    Toast.makeText(getContext(),"Invalid input: enter a code between 1 and 7", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}