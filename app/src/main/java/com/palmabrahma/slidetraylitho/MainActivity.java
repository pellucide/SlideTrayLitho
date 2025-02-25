package com.palmabrahma.slidetraylitho;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.facebook.litho.widget.Text;

/**
 * Main activity that demonstrates the use of EmojiDrawerComponent.
 */
public class MainActivity extends AppCompatActivity {

    private ComponentContext mComponentContext;
    private LithoView mLithoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the component context - required for all Litho operations
        mComponentContext = new ComponentContext(this);

        // Root component (main content)
        mLithoView = LithoView.create(
                this,
                MainComponent.create(mComponentContext)
                        .build()
        );

        // Add the LithoView to the activity
        setContentView(mLithoView);
    }
}