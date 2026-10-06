package com.featureflow.app;

public final class FeatureFlowApp {

    private FeatureFlowApp() {
    }

    public static void main(String[] args) {
        DaggerFeatureFlowComponent.create().demoRunner().run();
    }
}
