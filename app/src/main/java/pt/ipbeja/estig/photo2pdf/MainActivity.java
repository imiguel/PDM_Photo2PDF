package pt.ipbeja.estig.photo2pdf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// https://github.com/zxh/iText/blob/master/iText%20in%20Action%202nd%20Edition.pdf
// http://itextpdf.com/product/itextg
// http://www.vogella.com/tutorials/JavaPDF/article.html#createpdf


//spongy
//https://rtyley.github.io/spongycastle/
// http://stackoverflow.com/questions/6898801/how-to-include-the-spongy-castle-jar-in-android


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String PDF_MIME_TYPE = "application/pdf";
    private static final String RESULT_PDF_FILENAME_PREFIX =
            Environment.getExternalStorageDirectory() + File.separator;


    private ImageView mImageView;

    private List<File> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.files = new ArrayList<>();

        Button scanButton = (Button) findViewById(R.id.btnGetPhoto);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanButtonHandler();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.btnDelete);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelButtonHandler();
            }
        });

        Button stopButton = (Button) findViewById(R.id.btnToPDF);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopButtonHandler();
            }
        });

        this.mImageView = (ImageView) findViewById(R.id.imageView);
    }

    public void btnGetPhoto_onClick(View view) {

    }

    public void btnDelete_onClick(View view) {
    }

    public void btnToPDF_onClick(View view) {
    }

    /** Handles the scan button
     *
     */
    private void scanButtonHandler() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                // Create the File where the photo should go
                this.files.add(this.createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(this.lastPhotoFile()));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            } catch (IOException ex) {
                MainActivity.messageDialog(this, "Error", "Cannot create file!");
            }
        } else {
            MainActivity.messageDialog(this, "Error", "No camera!");
        }
    }

    //static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void cancelButtonHandler() {
        // to do
    }

    /** Get the last stored file
     *
     * @return the last stored file
     */
    private File lastPhotoFile() {
        return this.files.get(this.files.size() - 1);
    }

    // http://thinktibits.blogspot.pt/2011/06/convert-bmp-to-pdf-itext-java-example.html
    // from stop button
    // http://www.concretepage.com/itext/add-image-in-pdf-using-itext-in-java
    private void stopButtonHandler() {
        try {
            //Create Document Object
            Document document = new Document();
            String pdfFilename = MainActivity.RESULT_PDF_FILENAME_PREFIX + "Download/test.pdf";
            //Create PdfWriter for Document to hold physical file
            //http://api.itextpdf.com/itext/com/itextpdf/text/pdf/PdfWriter.html#getInstance(com.itextpdf.text.Document,%20java.io.OutputStream)
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilename));
            document.open();
            document.setMargins(0, 0, 0, 0);
            Rectangle r = document.getPageSize();
            r = new Rectangle((int)(r.getWidth() * 0.95 +0.5), (int)(r.getHeight() * 0.95 + 0.5));

            for (File file : this.files) {
                Image img = Image.getInstance("file:" + file.getAbsolutePath());
                img.setSpacingBefore(0);
                img.setSpacingAfter(0);
                img.scaleAbsolute(r);
                document.newPage();
                String name = file.getName();
                Paragraph p = new Paragraph(name);

                img.setAlignment(Image.MIDDLE);
                p.setAlignment(Element.ALIGN_CENTER);

                document.add(p);
                document.add(img);
            }
            document.close();

            System.out.println("Successfully Converted BMP to PDF in iText");
            this.openPDF(Uri.fromFile(new File(pdfFilename)));
        } catch (Exception i1) {
            i1.printStackTrace();
        }
    }

    /**
     * Creates a file with a time stamp in the name
     * @return the created file
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return  File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    /** Handle image
     * http://developer.android.com/training/camera/photobasics.html
     *
     * @param requestCode the requested action
     * @param resultCode the resutl code
     * @param data not being used
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView imgView = (ImageView) findViewById(R.id.imageView);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgView.setImageBitmap(imageBitmap);
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                Bitmap userImage = this.getBitmapFromFile(this.lastPhotoFile(), 5);
                mImageView.setImageBitmap(userImage);
            } catch (FileNotFoundException e) {
                // do nothing
            }
        }
    }

    /**
     * Get a Bitmap from the given file, scaled by scale
     * @param file file with image
     * @param scale value to scale image
     * @return the scaled Bitmap
     * @throws FileNotFoundException
     */
    private Bitmap getBitmapFromFile(File file, int scale) throws FileNotFoundException {
        FileInputStream in = new FileInputStream(file);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        return BitmapFactory.decodeStream(in, null, options);
    }

    /**
     * Open a pdf in localUri
     * @param localUri the pdf to open
     */
    public void openPDF(Uri localUri) {
        Intent i = new Intent(Intent.ACTION_VIEW); // REBENTA no genymotion
        //Intent i = new Intent( Intent.ACTION_SEND ); // não faz nada no genymotion

        //i.setDataAndType(Uri.fromFile(new File(this.RESULT_PDF_FILENAME_PREFIX)), PDF_MIME_TYPE);
        i.setDataAndType(localUri, PDF_MIME_TYPE);

        this.mImageView.getContext().startActivity(i);
    }

    /**
     * Utility mehtod to show a simple message dialog
     * @param context the dialog context
     * @param title the dialog title
     * @param message the fialog message
     */
    private static void messageDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }



}
