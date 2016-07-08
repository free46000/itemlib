package free.com.itemlib.item.view.content;


import free.com.itemlib.item.view.common.Validate;

/**
 * Created by free46000 on 2015/5/18 0018.
 */
public abstract class ItemInput extends ItemImpl {
    protected String viewTypeStr;
    protected Validate.Rule rule;

    /**
     * @param key 本录入View的值对应的存储key
     */
    public ItemInput(String key) {
        this.key = key;
    }

    public Validate.Rule getRule() {
        return rule;
    }

    public void setRule(Validate.Rule rule) {
        this.rule = rule;
    }
}
