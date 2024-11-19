package com.cool.gateway.config.center.api;

import com.cool.common.rule.Rule;

import java.util.List;

public interface RulesChangeListener {
    void onRulesChange(List<Rule> rules);
}
