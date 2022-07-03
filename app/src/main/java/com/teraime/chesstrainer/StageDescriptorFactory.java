package com.teraime.chesstrainer;

public class StageDescriptorFactory {

    public static StageDescriptor getStageDescriptor(int stage) {
        return new StageDescriptor(stage,"Test");
    }
}
