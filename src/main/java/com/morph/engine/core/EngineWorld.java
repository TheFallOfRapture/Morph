package com.morph.engine.core;

import com.morph.engine.entities.Entity;
import com.morph.engine.entities.EntityFactory;
import com.morph.engine.graphics.Color;
import com.morph.engine.graphics.Texture;
import com.morph.engine.graphics.components.RenderData;
import com.morph.engine.graphics.shaders.BasicLightShader;
import com.morph.engine.script.ScriptContainer;

public class EngineWorld extends ListWorld {
    public EngineWorld(Game game) {
        super(game);
    }

    @Override
    public void init(Game game) {
        Entity player = EntityFactory.INSTANCE.getCustomTintRectangle("player", 15, 15, new Color(0.1f, 0.1f, 0.1f), new BasicLightShader());
        ScriptContainer sc = new ScriptContainer(game, player);
        player.getComponent(RenderData.class).setTexture(new Texture("textures/testNormalMap.png"), 1);

        player.addComponent(sc);
        sc.addBehaviorAsync("EScript.kts");
        sc.addBehaviorAsync("TestPythonScript.py");

        addEntity(player);

        game.attachBehaviorAsync("TestBehavior.kts");
        game.attachBehaviorAsync("TestBehavior2.kts");
    }
}
