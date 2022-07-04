package com.teraime.chesstrainer;

public class StageDescriptor {

    int stage;
    String stageText;
    enum StageType {
        normal,
        not_normal
    };

    public StageDescriptor(int stage, String stageText) {
        this.stage = stage;
        this.stageText = stageText;
        switch (stage) {
            case 1:

        }

    }
}
