package ch.ethz.asl.dancebots.danceboteditor.model;

import ch.ethz.asl.dancebots.danceboteditor.R;
import ch.ethz.asl.dancebots.danceboteditor.utils.DanceBotEditorProjectFile;

/**
 * Created by andrin on 21.10.15.
 */

// 'Static' enum types are instantiated with the object
public enum MotorType implements MotionType {

    DEFAULT(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.motor_list_default_color), ""),
    STRAIGHT(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.motor_elem_color1), "S"),
    SPIN(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.motor_elem_color2), "P"),
    TWIST(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.motor_elem_color3), "T"),
    BACK_AND_FORTH(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.motor_elem_color4), "B"),
    CONSTANT(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.motor_elem_color5), "C"),
    WAIT(DanceBotEditorProjectFile.getInstance().getContext().getResources().getColor(R.color.motor_elem_color6), "W");

    private int mColor;
    private String mTag;

    MotorType(int color, String tag) {
        mColor = color;
        mTag = tag;
    }

    public int getColor() {
        return mColor;
    }

    public String getTag() {
        return mTag;
    }
}