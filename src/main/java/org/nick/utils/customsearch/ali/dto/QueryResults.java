package org.nick.utils.customsearch.ali.dto;

import org.nick.utils.customsearch.ali.SearchCriteria;

import java.util.Set;

/**
 * QueryResults
 *
 * @author VNikolaenko <dev@caple.co>
 * @version 02/01/2016
 * @see © 2016 Caple International B.V. - All Rights Reserved
 * See LICENSE file or http://caple.co for further details
 */
public class QueryResults {
    private SearchCriteria criteria;
    private Set<SearchResult> results;

    public QueryResults(SearchCriteria criteria, Set<SearchResult> results) {
        this.criteria = criteria;
        this.results = results;
    }

    public SearchCriteria getCriteria() {
        return criteria;
    }

    public Set<SearchResult> getResults() {
        return results;
    }
}
