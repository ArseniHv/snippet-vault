package com.snippetvault.analytics;

import java.util.List;

public record AnalyticsSummary(
        long totalPublicSnippets,
        List<LanguageStat> topLanguages,
        List<TagStat> topTags,
        List<MostViewedSnippet> mostViewedSnippets
) {}