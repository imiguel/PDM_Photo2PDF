package pt.ipbeja.estig.photo2pdf;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.Random;

/**
 * Created by miguel on 05/11/15.
 */
public class MyIntents {

    private static int startIntent(Activity activity, Intent intent, boolean result)
    {
        int codeForResult = 0;

        if (result == false) {
            activity.startActivity(intent);
        } else {
            Random random = new Random();
            codeForResult = random.nextInt(32655);
            activity.startActivityForResult(intent, codeForResult);
        }

        return codeForResult;
    }

    public static int captureImage(Activity activity, String filename)
    {
        Uri uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        return MyIntents.startIntent(activity, intent, true);
    }
}
