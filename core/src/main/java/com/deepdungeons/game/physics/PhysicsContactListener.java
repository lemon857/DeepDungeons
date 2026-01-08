package com.deepdungeons.game.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.ArrayList;
import java.util.List;

public class PhysicsContactListener implements ContactListener {

  private final List<ObjectsCollisionProcessor> objectsCollisionProcessors;

  public PhysicsContactListener() {
      objectsCollisionProcessors = new ArrayList<>();
  }

  public void addProcessor(ObjectsCollisionProcessor processor) {
    objectsCollisionProcessors.add(processor);
  }

  @Override
  public void beginContact(Contact contact) {
    processContact(contact.getFixtureA().getBody().getUserData(), contact.getFixtureB().getBody().getUserData());
  }

  @Override
  public void endContact(Contact contact) { }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) { }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) { }

  private void processContact(Object a, Object b) {
    for (ObjectsCollisionProcessor processor : objectsCollisionProcessors) {
      processor.process(a, b);
    }
  }
}