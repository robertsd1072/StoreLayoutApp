package DJR_Store_Layout.GridData;

import javafx.scene.shape.Line;

public class PNode
{
    public Line hLine;
    public Line vLine;

    public PNode(Line x, Line y)
    {
        hLine = x;
        vLine = y;
    }

    public void setNulled(boolean hmm)
    {
        hLine.setVisible(!hmm);
        vLine.setVisible(!hmm);
    }
}