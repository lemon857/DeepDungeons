package com.deepdungeons.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class PhysicsContactListener implements ContactListener {
  @Override
  public void beginContact(Contact contact) {
    String name_a = (String)contact.getFixtureA().getBody().getUserData();
    String name_b = (String)contact.getFixtureB().getBody().getUserData();

    System.out.printf("Body a: %s, Body b: %s\n", name_a, name_b);
  }

@Override
public void endContact(Contact contact) {
  
}

@Override
public void preSolve(Contact contact, Manifold oldManifold) {
  // TODO Auto-generated method stub
  // throw new UnsupportedOperationException("Unimplemented method 'preSolve'");
}

@Override
public void postSolve(Contact contact, ContactImpulse impulse) {
  // TODO Auto-generated method stub
  // throw new UnsupportedOperationException("Unimplemented method 'postSolve'");
}
}