package com.infinite.common.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.infinite.common.R;

import java.util.ArrayList;
import java.util.List;

public class CardView extends View {
    public CardView(Context context) {
        super(context);
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private List<Card> cards = new ArrayList<>();

    private void init() {
        cards.add(new Card(BitmapFactory.decodeResource(getResources(), R.mipmap.a), 0));
        cards.add(new Card(BitmapFactory.decodeResource(getResources(), R.mipmap.a), cards.get(0).getWidth() / 4));
        cards.add(new Card(BitmapFactory.decodeResource(getResources(), R.mipmap.a), cards.get(0).getWidth() / 2));
        cards.add(new Card(BitmapFactory.decodeResource(getResources(), R.mipmap.a), cards.get(0).getWidth() * 3 / 4));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (int i = 0; i < cards.size(); i++) {
            Card card=cards.get(i);
            if (i==cards.size()-1){
                card.setLast(true);
                card.draw(canvas, null);
            }else{
                card.draw(canvas,cards.get(i+1));
            }
        }


    }
}
