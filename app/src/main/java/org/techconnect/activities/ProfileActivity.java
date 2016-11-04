package org.techconnect.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.centum.techconnect.R;
import org.techconnect.asynctasks.UpdateUserAsyncTask;
import org.techconnect.misc.auth.AuthManager;
import org.techconnect.model.User;
import org.techconnect.sql.TCDatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {
    TableLayout skills_table;
    List<ImageButton> row_buttons;
    User temp_user;
    List<String> tmp_skills; //Hold onto the actual final set of skills for the user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        //Here, we access the current User from the Database, create a temporary user in case we need to update
        User user = TCDatabaseHelper.get(this).getUser(AuthManager.get(this).getAuth().getUserId());
        temp_user = new User();

        tmp_skills = new ArrayList<String>();
        temp_user.set_id(user.get_id());
        temp_user.setEmail(user.getEmail());
        temp_user.setName(user.getName());
        temp_user.setCountryCode(user.getCountryCode());
        temp_user.setCountry(user.getCountry());
        temp_user.setOrganization(user.getOrganization());

        //Setup the button to save the data
        final Button saveButton = (Button) findViewById(R.id.save_button);

        //Add return arrow to action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Setup the Toolbar Title
        CollapsingToolbarLayout layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        layout.setTitle(user.getName());

        //Add organization, email, and skills to the list below
        final TextView org = (TextView) findViewById(R.id.profile_work);
        final TextView email = (TextView) findViewById(R.id.profile_email);
        org.setText(user.getOrganization());
        email.setText(user.getEmail());

        //Create all rows from list of skills in profile
        skills_table = (TableLayout) findViewById(R.id.skills_table);
        row_buttons = new ArrayList<ImageButton>(); //Store reference of where buttons are

        for (int i = 0; i < user.getExpertises().size(); i++) {
            TableRow toAdd = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_skill,null,false);
            final ImageButton row_button = (ImageButton) toAdd.findViewById(R.id.skill_icon);
            row_buttons.add(row_button);
            row_button.setTag(i); //View that the button belongs to
            row_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //We want to delete the entire row that it belongs to
                    skills_table.removeViewAt(row_buttons.indexOf(row_button));
                    tmp_skills.remove(row_buttons.indexOf(row_button));
                    row_buttons.remove(row_button);
                }
            });
            TextInputLayout addSkill = (TextInputLayout) toAdd.findViewById(R.id.edit_skill_layout);
            addSkill.setVisibility(View.GONE);
            TextView toAddText = (TextView)  toAdd.findViewById(R.id.skill_text);
            toAddText.setText(user.getExpertises().get(i));
            toAddText.setVisibility(View.VISIBLE);
            tmp_skills.add(user.getExpertises().get(i));
            skills_table.addView(toAdd);
        }

        //Register the edit text fields for changing account info
        final TextInputLayout edit_org_layout = (TextInputLayout) findViewById(R.id.edit_work_layout);
        final TextInputLayout edit_email_layout = (TextInputLayout) findViewById(R.id.edit_email_layout);
        final EditText edit_org = (EditText) findViewById(R.id.edit_work_text);
        final EditText edit_email = (EditText) findViewById(R.id.edit_email_text);
        edit_org.setText(user.getOrganization());
        edit_email.setText(user.getEmail());

        //Setup the edit button listeners to make changes to the profile info
        final ImageButton editWork = (ImageButton) findViewById(R.id.edit_work_button);
        editWork.setOnClickListener(new View.OnClickListener() {
            boolean isEditing = false;
            @Override
            public void onClick(View view) {

                if (!isEditing) {
                    org.setVisibility(View.GONE);
                    email.setVisibility(View.GONE);
                    edit_org_layout.setVisibility(View.VISIBLE);
                    edit_email_layout.setVisibility(View.VISIBLE);
                    editWork.setImageResource(R.drawable.ic_done_black_24dp);
                } else {
                    org.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    org.setText(edit_org.getText());
                    email.setText(edit_email.getText());
                    temp_user.setOrganization(edit_org.getText().toString());//Update Reference
                    temp_user.setEmail(edit_email.getText().toString());

                    edit_org_layout.setVisibility(View.GONE);
                    edit_email_layout.setVisibility(View.GONE);
                    editWork.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    saveButton.setVisibility(View.VISIBLE);
                }
                isEditing = !isEditing;
            }
        });


        final ImageButton editSkill = (ImageButton) findViewById(R.id.edit_skill_button);
        editSkill.setOnClickListener(new View.OnClickListener() {
            boolean isAdding = false;
            @Override
            public void onClick(View view) {
                //Add a new row to the list of skills or deleting old skills
                if (!isAdding) {
                    for (ImageButton button : row_buttons) {
                        button.setClickable(true);
                        button.setImageResource(R.drawable.ic_close_black_24dp);
                    }
                    onRowAddRequest();
                    editSkill.setImageResource(R.drawable.ic_done_black_24dp);
                } else { //Stopping adding
                    skills_table.removeViewAt(skills_table.getChildCount() -1);//Always delete the last one
                    row_buttons.remove(row_buttons.size() - 1); //Remove the last one
                    for (ImageButton button: row_buttons) {
                        button.setImageResource(R.drawable.ic_build_black_24dp);
                        button.setClickable(false);
                    }
                    editSkill.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    saveButton.setVisibility(View.VISIBLE);
                }
                isAdding = !isAdding;
            }
        });
    }

    //This comes from the Options Menu on the upper right
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private TableRow onRowAddRequest() {
        final TableRow toAdd;
        final TextInputLayout inputLayout;
        final ImageButton icon;
        final EditText add_skill;
        final TextView skill_text;

        toAdd = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_skill,null,false);
        inputLayout = (TextInputLayout) toAdd.findViewById(R.id.edit_skill_layout);
        icon = (ImageButton) toAdd.findViewById(R.id.skill_icon);
        icon.setImageResource(R.drawable.ic_add_box_black_24dp);
        add_skill = (EditText) toAdd.findViewById(R.id.edit_skill_text);
        skill_text = (TextView) toAdd.findViewById(R.id.skill_text);

        icon.setOnClickListener(new View.OnClickListener() {
            boolean adding = true; //Initially, adding a new row
            @Override
            public void onClick(View view) {
                //User actually entered something
                if (adding) {
                    if (add_skill.getText().length() > 0) {
                        skill_text.setText(add_skill.getText());
                        tmp_skills.add(add_skill.getText().toString());//Add to temp user
                        icon.setImageResource(R.drawable.ic_close_black_24dp);
                        skill_text.setVisibility(View.VISIBLE);
                        inputLayout.setVisibility(View.GONE);
                        onRowAddRequest();
                        adding = false;
                    }
                } else {
                    //We want to delete the entire row that it belongs to
                    skills_table.removeViewAt(row_buttons.indexOf(icon));
                    tmp_skills.remove(row_buttons.indexOf(icon));//Remove expertise
                    row_buttons.remove(icon);
                }
            }
        });

        row_buttons.add(icon);
        skills_table.addView(toAdd);
        return toAdd;
    }

    public void writeUserToDatabase(View v) throws ExecutionException, InterruptedException {
        //Use the temp_user object to write any user changes to the database
        final Context context = this;
        temp_user.setExpertises(tmp_skills);
        new UpdateUserAsyncTask(context) {
            ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = ProgressDialog.show(ProfileActivity.this, getString(R.string.save_changes), null, true, false);
            }

            @Override
            protected void onPostExecute(User u) {
                pd.dismiss();
                pd = null;
                if (u != null) {
                    TCDatabaseHelper.get(context).upsertUser(u);

                } else {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.error)
                            .setMessage(R.string.failed_update_user)
                            .show();

                }
            }
        }.execute(temp_user);

        v.setVisibility(View.GONE);
    }

}
