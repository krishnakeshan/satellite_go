package com.piedpipar.satellitego;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {
    //Properties
    private ArFragment arFragment;

    private ModelRenderable planetRenderable;
    private ModelRenderable auraSatRenderable;

    //Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get arFragment
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_ar_fragment);

        //build planet model renderable
        ModelRenderable.builder().setSource(this, Uri.parse("Earth.sfb")).build().thenAccept(renderable -> planetRenderable = renderable);
        ModelRenderable.builder().setSource(this, R.raw.satellite).build().thenAccept(renderable -> auraSatRenderable = renderable);

        //set plane tap listener
        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                TransformableNode planetNode = new TransformableNode(arFragment.getTransformationSystem());
                planetNode.setParent(anchorNode);
                planetNode.setRenderable(auraSatRenderable);
                planetNode.select();
            }
        });
    }
}
