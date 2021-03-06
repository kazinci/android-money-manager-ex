package com.money.manager.ex.assetallocation;

import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.util.Xml;
import android.webkit.WebView;
import android.widget.TextView;

import com.money.manager.ex.Constants;
import com.money.manager.ex.R;
import com.money.manager.ex.common.BaseFragmentActivity;
import com.money.manager.ex.currency.CurrencyService;
import com.money.manager.ex.domainmodel.AssetClass;
import com.money.manager.ex.servicelayer.AssetAllocationService;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class AssetAllocationOverviewActivity
    extends BaseFragmentActivity {

    public static final String VALUE_FORMAT = "%,.2f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_allocation_overview);

        // get asset allocation
        AssetAllocationService service = new AssetAllocationService(this);
        AssetClass allocation = service.loadAssetAllocation();

        // create a HTML display.
        String html = createHtml(allocation);
        displayOverview(html);
    }

    private String createHtml(AssetClass allocation) {
        String html = "<html><body style='background: lightgray; padding: 0;'>";

        html += getSummaryRow(allocation);

        html += getList(allocation.getChildren());

        html += "</body></html>";
        return html;
    }

    private void displayOverview(String html) {
        WebView overview = (WebView) this.findViewById(R.id.overviewWebView);
        overview.loadData(html, "text/html", null);
    }

    /**
     * Create a list with child elements.
     * @param children Asset Allocation/Class
     * @return HTML list (ul) of the child Asset Classes with information.
     */
    private String getList(List<AssetClass> children) {
        String html = "";
        if (children.size() == 0) return html;

        html += "<ul style='padding-left: 20px;'>";

        for(AssetClass child : children) {
            html += getAssetRow(child);
        }

        html += "</ul>";
        return html;
    }

    private String getSummaryRow(AssetClass allocation) {
        String html = "";
        CurrencyService currencyService = new CurrencyService(this);

        html += "<p>" +
            allocation.getName() + ", " +
            currencyService.getBaseCurrencyCode() + " " +
            String.format(VALUE_FORMAT, allocation.getCurrentValue().toDouble()) +
            "</p>";
        return html;
    }

    private String getAssetRow(AssetClass allocation) {
        String color;
        // style='list-style-position: inside;'
        String html = "<li>";

        // Name
        html += allocation.getName() + ", ";
        // diff %
        color = allocation.getDiffAsPercentOfSet().toDouble() >= 0 ? "green" : "darkred";
        html += "<span style='color: " + color + ";'>";
        html += allocation.getDiffAsPercentOfSet();
        html += " %</span>";

        html += ", ";

        // difference amount
        color = allocation.getDifference().truncate(2).toDouble() >= 0 ? "green" : "darkred";
        html += "<span style='color: " + color + ";'>";
        html += String.format(VALUE_FORMAT, allocation.getDifference().toDouble());
        html += "</span>";

        html += "<br/>";

        // Allocation
        html += String.format(VALUE_FORMAT, allocation.getAllocation().toDouble()) + "/";
        color = allocation.getDifference().toDouble() > 0 ? "green" : "darkred";

        // current allocation
        html += "<span style='color: " + color + "; font-weight: bold;'>";
        html += String.format(VALUE_FORMAT, allocation.getCurrentAllocation().toDouble()) +
                "</span>";

        html += ", ";
//        html += "<br/>";

        // Value
        html += String.format(VALUE_FORMAT, allocation.getValue().toDouble()) + "/" +
            String.format(VALUE_FORMAT, allocation.getCurrentValue().toDouble());
//        html += ", ";

        // Child asset classes
        html += getList(allocation.getChildren());

        html += "</li>";

        return html;
    }
}
