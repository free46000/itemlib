package free.com.itemlib.item.animation;

import android.animation.Animator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import free.com.itemlib.item.view.ItemViewHolder;

/**
 * Created by wzx on 2016/8/12.
 */
public class AnimationLoader {
    protected int lastAnimIndex = -1;
    protected boolean isAnimEnable;
    protected boolean isShowAnimWhenFirst;
    protected BaseAnimation animation;
    protected long animDuration = 400L;
    protected Interpolator interpolator = new LinearInterpolator();


    public void clear() {
        lastAnimIndex = -1;
    }

    /**
     * 打开加载动画
     *
     * @param animation           BaseAnimation
     * @param isShowAnimWhenFirst boolean 是否只有在初次加载的时候才使用动画
     */
    public void enableLoadAnimation(BaseAnimation animation, boolean isShowAnimWhenFirst) {
        this.isAnimEnable = true;
        this.isShowAnimWhenFirst = isShowAnimWhenFirst;
        this.animation = animation == null ? new SlideInLeftAnimation() : animation;
    }


    public void addAnimation(ItemViewHolder holder) {
        if (isAnimEnable) {
            if (!isShowAnimWhenFirst || holder.location > lastAnimIndex) {
                for (Animator anim : animation.getAnimators(holder.getItemView())) {
                    startAnim(anim, holder.location);
                }
                lastAnimIndex = holder.location;
            }
        }
    }

    protected void startAnim(Animator anim, int index) {
        anim.setDuration(animDuration).start();
        anim.setInterpolator(interpolator);
    }


    public boolean isAnimEnable() {
        return isAnimEnable;
    }

    public void setAnimEnable(boolean animEnable) {
        isAnimEnable = animEnable;
    }

    public boolean isShowAnimWhenFirst() {
        return isShowAnimWhenFirst;
    }

    public void setShowAnimWhenFirst(boolean showAnimWhenFirst) {
        isShowAnimWhenFirst = showAnimWhenFirst;
    }

    public BaseAnimation getAnimation() {
        return animation;
    }

    public void setAnimation(BaseAnimation animation) {
        this.animation = animation;
    }

    public long getAnimDuration() {
        return animDuration;
    }

    public void setAnimDuration(long animDuration) {
        this.animDuration = animDuration;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }
}
