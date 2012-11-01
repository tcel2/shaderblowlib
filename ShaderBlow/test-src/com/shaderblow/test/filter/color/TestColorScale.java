package com.shaderblow.test.filter.color;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.asset.plugins.HttpZipLocator;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import com.shaderblow.filter.colorscale.ColorScaleFilter;

public class TestColorScale extends SimpleApplication {

	private FilterPostProcessor fpp;
	private boolean enabled = true;
	private ColorScaleFilter colorScale;

	public static void main(final String[] args) {
		final TestColorScale app = new TestColorScale();
		app.start();
	}

	@Override
	public void simpleInitApp() {

		this.assetManager.registerLocator("assets", FileLocator.class);

		this.flyCam.setMoveSpeed(10);

		final Node mainScene = new Node();
		mainScene.attachChild(SkyFactory.createSky(this.assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
		this.assetManager.registerLocator("http://jmonkeyengine.googlecode.com/files/wildhouse.zip",
				HttpZipLocator.class);
		// this.assetManager.registerLocator("wildhouse.zip", ZipLocator.class);
		final Spatial scene = this.assetManager.loadModel("main.scene");
		mainScene.attachChild(scene);
		this.rootNode.attachChild(mainScene);

		final DirectionalLight dl = new DirectionalLight();
		dl.setDirection(new Vector3f(-0.8f, -0.6f, -0.08f).normalizeLocal());
		dl.setColor(new ColorRGBA(1, 1, 1, 1));
		this.rootNode.addLight(dl);

		final AmbientLight al = new AmbientLight();
		al.setColor(new ColorRGBA(1.5f, 1.5f, 1.5f, 1.0f));
		this.rootNode.addLight(al);

		this.flyCam.setMoveSpeed(15);

		this.fpp = new FilterPostProcessor(this.assetManager);
		this.fpp.setNumSamples(4);
		this.colorScale = new ColorScaleFilter(new ColorRGBA(255f / 255f, 66f / 255f, 20f / 255f, 1.0f), 0.7f);
		this.fpp.addFilter(this.colorScale);
		this.viewPort.addProcessor(this.fpp);
		this.initInputs();
	}

	private void initInputs() {
		this.inputManager.addMapping("toggle", new KeyTrigger(KeyInput.KEY_SPACE));
		this.inputManager.addMapping("DensityUp", new KeyTrigger(KeyInput.KEY_Y));
		this.inputManager.addMapping("DensityDown", new KeyTrigger(KeyInput.KEY_H));

		final ActionListener acl = new ActionListener() {

			@Override
			public void onAction(final String name, final boolean keyPressed, final float tpf) {
				if (name.equals("toggle") && keyPressed) {
					if (TestColorScale.this.enabled) {
						TestColorScale.this.enabled = false;
						TestColorScale.this.viewPort.removeProcessor(TestColorScale.this.fpp);
					} else {
						TestColorScale.this.enabled = true;
						TestColorScale.this.viewPort.addProcessor(TestColorScale.this.fpp);
					}
				}

			}
		};

		final AnalogListener anl = new AnalogListener() {

			@Override
			public void onAnalog(final String name, final float isPressed, final float tpf) {
				if (name.equals("DensityUp")) {
					TestColorScale.this.colorScale
							.setColorDensity(TestColorScale.this.colorScale.getColorDensity() + 0.001f);
					System.out.println("ColorScale density : " + TestColorScale.this.colorScale.getColorDensity());
				}
				if (name.equals("DensityDown")) {
					TestColorScale.this.colorScale
							.setColorDensity(TestColorScale.this.colorScale.getColorDensity() - 0.001f);
					System.out.println("ColorScale density : " + TestColorScale.this.colorScale.getColorDensity());
				}
			}
		};

		this.inputManager.addListener(acl, "toggle");
		this.inputManager.addListener(anl, "DensityUp", "DensityDown");

	}
}