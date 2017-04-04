package hawc.eecs582.com.hawc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Jaydon on 3/12/2017.
 */
public class GetPictureActivity extends Activity implements View.OnClickListener
{
    Toolbar mToolbar;
    private NavigationView mNavView;
    private DrawerLayout mDrawerLayout;
    private Menu mNavMenu;

    private View mNavHeaderLayout;

    String mTitle = "Upload A Photo";


    private EditText title,desc;
    private Button btnselectpic;
    private ImageButton uploadButton;
    private ImageView imageview;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;

    private String upLoadServerUri = null;
    private String imagepath=null;

    private Context ctx;

    private Bitmap theBitmap;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment);

        FrameLayout layout = (FrameLayout) findViewById(R.id.container_body);
        LayoutInflater.from(this).inflate(R.layout.activity_main2, layout, true);

        theBitmap = null;

        ctx = this.getApplicationContext();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setActionBar(mToolbar);
        }
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(true);
        this.getActionBar().setTitle(mTitle);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.nav_drawer_open, R.string.nav_drawer_close)
        {
            public void onDrawerOpened(View drawerView) {

                Log.d("ASDf", "Hello");
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavView = (NavigationView) findViewById(R.id.nav_view);

        mNavHeaderLayout = mNavView.getHeaderView(0);

        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                displayFragment(menuItem.getItemId(), menuItem.getTitle().toString());
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mNavMenu = mNavView.getMenu();



        uploadButton = (ImageButton)findViewById(R.id.btnSend);
        btnselectpic = (Button)findViewById(R.id.btnGallery);
        imageview = (ImageView)findViewById(R.id.imgView);

        btnselectpic.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        //uploadButton.setEnabled(false);

        //upLoadServerUri = "http://eecs582project.x10.mx/upload.php";
        //upLoadServerUri = "https://people.eecs.ku.edu/~lbutler/Team14/upload.php";
        upLoadServerUri = "http://66.45.136.217/OCR-master/OCR-master/ocrUpload.php";
        ImageView img= new ImageView(this);





    }

    @Override
    public void onClick(View arg0) {
        if(arg0==btnselectpic)
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
        }
        else if (arg0==uploadButton) {

            //dialog = ProgressDialog.show(MainActivity2.this, "", "Uploading file...", true);
            new Thread(new Runnable() {
                public void run() {

                    if(theBitmap == null)
                    {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ctx, "You have not selected a picture yet.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ctx, "Sending Picture and Processing.\n  This may take a moment.", Toast.LENGTH_LONG).show();
                            }
                        });
                        uploadFile(theBitmap);
                    }


                }
            }).start();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Bitmap photo = (Bitmap) data.getData().getPath();
            //Uri imagename=data.getData();
            Log.d("ASDF", "Picture found");
            Uri selectedImageUri = data.getData();
            Log.d("ASDF",selectedImageUri.getPath());
            imagepath = trythis(selectedImageUri);//getPath(selectedImageUri);
            Bitmap bitmap=BitmapFactory.decodeFile(imagepath);
            imageview.setImageBitmap(bitmap);

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImageUri);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            Matrix matrix = new Matrix();

            matrix.postRotate(90);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp,bmp.getWidth()/8,bmp.getHeight()/8,true);

            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            imageview.setImageBitmap(rotatedBitmap);

            theBitmap = rotatedBitmap;

        }

    }

    private void displayFragment(int id, String title) {

        boolean action = false;

        switch (id) {

            case R.id.upload_picture: {



                break;
            }

            case R.id.upload_code: {

                Intent intent = new Intent(GetPictureActivity.this, EditCodeActivity.class);
                intent.putExtra("message", "#include <iostream>\n int main()\n{\nstd::cout << \" Hello World \" << std::endl; \n std::cout<<\"another line\" << std::endl; \n}\n");
                startActivity(intent);

                break;
            }

            case R.id.view_output: {

                Intent intent = new Intent(GetPictureActivity.this, ViewOutputActivity.class);
                intent.putExtra("message", "");
                startActivity(intent);

                break;
            }



        }


    }

    String trythis(Uri uri)
    {
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor c = getContentResolver().query(uri, filePath, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePath[0]);
        String picturePath = c.getString(columnIndex);
        return picturePath;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public int uploadFile(Bitmap bmp) {

        //sourceFileUri.replace(sourceFileUri, "ashifaq");
        //

        int day, month, year;
        int second, minute, hour;
        GregorianCalendar date = new GregorianCalendar();

        day = date.get(Calendar.DAY_OF_MONTH);
        month = date.get(Calendar.MONTH);
        year = date.get(Calendar.YEAR);

        second = date.get(Calendar.SECOND);
        minute = date.get(Calendar.MINUTE);
        hour = date.get(Calendar.HOUR);

        String name=(hour+""+minute+""+second+""+day+""+(month+1)+""+year);
        String tag=name+".png";
        String fileName = tag;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = null;
        try {

            sourceFile = bitmapToFile(bmp);

        }
        catch(Exception e)
        {
            Log.d("ASDF", "Exception occured");
        }


        if (!sourceFile.isFile()) {

            //dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :" + imagepath);



            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());
                Log.d("ASDF", fileName);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);




                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);


                Log.d("ASDF", "Response Code Section");
                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n";
                            Toast.makeText(GetPictureActivity.this, "File Upload Complete.", Toast.LENGTH_LONG).show();
                        }
                    });
                    String stringResult = "";
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null)
                            stringResult += line;
                    }
                    in.close();
                    Log.d("ASDF", stringResult);
                    if(stringResult != "fail")
                    {
                        Intent intent = new Intent(GetPictureActivity.this, EditCodeActivity.class);
                        intent.putExtra("message", stringResult);
                        startActivity(intent);
                    }
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(GetPictureActivity.this, "There was an error.\n Please try again.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                //dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ctx, "Unable To Send File.\nPlease try again later.", Toast.LENGTH_LONG).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                //dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ctx, "Trouble Connecting with Server.\nPlease try again later.", Toast.LENGTH_LONG).show();
                    }
                });
            }
            //dialog.dismiss();
            return serverResponseCode;

        }
    }

    File bitmapToFile(Bitmap bmp) throws Exception
    {
        File f = new File(this.getCacheDir(), "theFile.png");
        f.createNewFile();

        //Convert bitmap to byte array
        Bitmap bitmap = bmp;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return f;
    }

}
