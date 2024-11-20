package com.cool.gateway.config.center.api;

import com.cool.common.config.Rule;

import java.util.List;

public interface RulesChangeListener {
    void onRulesChange(List<Rule> rules);
}
