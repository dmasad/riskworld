MINING THE UNCONVENTIONAL ANALYTICS OF ENERGY SECURITY
======================================================


# I. KEY POLICY QUESTION
What risks to oil security are posed by cascading political supply shocks? Can they be identified using a computer simulation?

# II. EXSUM
Energy security is placed at risk when energy producers experience internal or external crises or conflicts. Crises in major consumers may also pose a risk for major producers who depend on energy prices for internal stability. The contagion of conflict from country to country increases the risk of widespread  disruptions to the international oil system. Such risks may be mitigated by stockpiles and excess capacity. A computer simulation encodes these assumptions, combines them with data on the present international oil trade network, and generates ranges of notional future outcomes. It indicates that countries with limited trade relationships are most vulnerable to energy risks, that stockpiles are sufficient to address the majority of short-term shocks but that the probability of large-magnitude shocks must also be accounted for.

# III. TIME FRAME: Simulating the ‘Long Present’
The structure of the international system is constantly changing, and the trade in oil is no exception. Energy demand in different countries rises and falls seasonally [[CITE]], and changes due to long-term trends in consumption and efficiency; similarly, old oil wells run dry while new reserves are discovered, and new extraction technology introduced [[CITE]]. The geopolitical environment shifts as well, with countries ceasing imports from and exports to some counterparts while beginning them for others, due to not only economic but diplomatic and security concerns. Similarly, as countries’ own governance and economies improve or decline, they become more or less stable, and more or less likely to experience internal instability or enter into external conflict. 
This model does not incorporate most of these elements. They are by their very nature difficult to foresee with any degree of precision, and in order to do so we would need to introduce complicated and weakly-supported assumptions. Instead, I focus on modeling what I call the Long Present, the continuation of the current structure of the international oil system into the near future. 
This assumption is less limiting than it may appear at first. For the most part, the international oil system appears to change slowly. The major trade relationships which dominate the international oil market remain largely fixed from year to year, even while the volume of trade fluctuates. Swings in demand are likely to affect all suppliers similarly, just as the effects of new production will reach all consumers – indeed, these assumptions are built into the model. Furthermore, there is a significant time lag between the discovery of new potential sources of oil and that oil’s appearance on the market, particularly for discoveries large enough to shift the balance of trade significantly. 

# IV. ASSUMPTIONS

## SUPPLY SHOCKS
The model ties geopolitics to energy security through the mechanism of supply shocks. Generally speaking, a supply shock refers to a rise in oil prices due to an active disruption in production (as opposed to sudden increases in demand or speculator activity, for example). Recent research suggests that oil prices are only loosely linked to supply disruptions [[CITE]], due to anticipation and financial speculation. Nevertheless, oil prices themselves are only an incomplete measure of energy security: if a major source of oil is disrupted, that oil (and the energy it contains) is unavailable. 
The majority of significant supply disruptions of the past decades have been driven by conflict and instability, from general strikes (Venezuela 2002-03) to all-out war (1980-88 Iran-Iraq War). It is such disruptions that this model focuses on.

## MODEL STRUCTURE

* STATES are the unit of analysis, with oil companies, international organizations and other actors only included implicitly. States have a fixed network of trade partners, and baseline demand and supply for oil, which change in response to crises.
* Each month, a state that is not in crisis may experience one with a fixed probability based on an estimate of its stability. The crisis length is drawn from a power law distribution, as explained below. 
* Optionally, a crisis may spread from a state to its neighbors. The more unstable the neighbors, the more likely the crisis is to spread.
* When a state is in crisis, its participation in the international oil market is considered at risk, both as an importer and exporter. The model measures the ratios of oil supply and demand that are not at risk, both state-by-state and worldwide. These are the primary measures of energy security.
* If a country is experiencing a supply shock, its trade partners may temporarily increase their own production in order to alleviate the shock. 

# V. ANALYSIS
## DRIVERS
The model is instantiated with data representing the current state of the world, which will remain fixed throughout the course of each model run. These data represent the 'drivers' of the analysis, the geopolitical and energy environment within which countries operate. 
In this paper, I use the most recent data available, aiming to capture the leading edge of the Long Present for forecasting purposes. However, this is not a requirement: past data could just as easily be used in order to compare model output to real history, and counterfactual data can be used for scenario analysis. 

The model describes the world in the following ways:

* THE OIL TRADE NETWORK: Essentially every country imports at least some of its oil from other countries. Many countries also export oil, even if only a relatively small volume. The most comprehensive data on these import-export relationships is stored in the United Nations-maintained COMTRADE database [[CITE]], which in turn is derived from country's own reports. While these reports are not perfect, I obtain increased validity by looking at both sides of each transaction, comparing countries' reported exports and imports. While many key oil exporters (most notably Saudi Arabia and Venezuela) do not break their reported exports down to the country level, the majority of their trade partners report the volume of oil imported from them. This data is processed to obtain as complete a picture as possible for the network of oil imports and exports. 
* OIL SUPPLY AND DEMAND: The majority of each country's supply of and demand for oil is accounted for the the trade data. The majority of countries import nearly all their oil, and domestic production accounts for only a small fraction of their overall demand. For key countries -- the US, Saudi Arabia and others -- I identify their production for domestic consumption. For all others, I assume that baseline demand is equal to total imports.
* Each country-to-country trade relationship is analyzed to compute the fraction that it represents of the source country's exports and the target country's imports. If a source country enters crisis, this is the fraction of the target country's imports that are at risk.
* POLITICAL INSTABILITY: A country's political stability is the key determinant of its likelihood to enter crisis. Political stability data was obtained from the Economist Intelligence Unit's Political Instability Index [[CITE]], which is in turn based on work done by the US Government-funded Political Instability Task Force [[CITE]].  I use this index as the underlying measure of instability, such that a country with a Political Stability Index value of 10 (the highest possible value) has a XX% chance of entering a period of crisis or instability every month – corresponding to an overall 80% chance over a two-year period. 
* CONTAGION is a special case here, a driver which remains fixed throughout each model run, but may be changed between them. The topic of whether (and how) internal unrest spreads from country to country is still under active debate within the political science literature. While there is some evidence [[CITE]] to suggest that the phenomena is exaggerated overall, there are cases where it can be clearly observed to occur – most recently, the spread of the Arab Spring across the Middle East. Thus, the model explores both futures where conflict contagion occurs and when it does not. When contagion is turned on, a crisis may spread from one country to its immediate neighbors; otherwise, crises are treated as independent of one another.  

## VARIABLES

Energy security is determined by many variables which change over time. Some of these variables are under countries' control, some involve the complex interactions between countries, while others still depend on the varagies of chance and science. Only a small subset of these variables have been incorporated into the model to date, but there is no reason that more could not be included as well.

### MODEL VARIABLES

* CRISES are the key variable that determines each country's energy security at different times. In the model, crises arise at random as described above: countries cannot intentionally affect or prevent them. Theoretically, however, a country's ability to prevent crises is factored into its stability index. Thus, crises can be treated as occuring when these efforts fail. In reality, of course, countries are capable of modifying their crisis-prevention efforts, both internally and abroad. Thus, the model will help highlight which crises may pose the greatest risk to other countries, and suggest where crisis-prevention or -mitigation resources may be best spent to preserve energy security.
* LENGTH OF CRISES is randomly drawn from a power law distributions. Powe laws are known to characterize inter-state conflicts, terrorist attacks, financial crises and other complex events. Power laws have small-magnitude events more likely than large-magnitude ones, with an important caveat – the large values remain possible, and in fact may be arbitrarily large.
*ENERGY SECURITY is a measure of a country's current condition, rather than a variable that is strictly under its control. Nevertheless, a country's energy security is an input into several of the following variables, and so I will describe these measures here first. Energy security is measured in two ratios:
    * **Import Demand / Supply** is the primary measure of energy availability. It is simply the ratio of a country's (fixed) total demand to the sum total of its imports from countries that are not currently in crisis. The baseline for this ratio is 1. As oil sources enter crisis, the ratio will increase, as demand outpaces safe supply. If another country increases its oil production (as described below), the ratio may dip below 1. This captures situations when a release of reserves may in fact create a glut of oil, particularly in countries which are less affected by ongoing crises. If the ratio rises above a predefined threshold, a country is considered to be experiencing a SUPPLY SHOCK.
    * **Export Demand / Supply** is a measure mostly relevant to large oil exporters, and measures the ratio of overall production-for-export to the demand by trade partners who are not themselves currently in crisis. This measure is primarily in place to capture the risk posed to major exporters by demand shocks [[CITE]], such as the one that occured in the US and elsewhere following the 2007-08 economic crisis, which risk pushing the price of oil below a break-even point [[CITE]]. 
    * In addition, the model tracks a global **Current Demand / Current Supply** ratio: the sum of demand from all countries not currently in crisis divided by the sum of all exports from countries not currently in crisis. This is meant as an approximate proxy for the price of oil. 
    * OUTPUT RESERVE refers to countries' ability to *rapidly* increase oil output in response to a crisis. The main country for which this is relevant is Saudi Arabia, which has claimed an ability to increase output by up to 25% if needed. This ability is modeled as follows: if country B imports oil from country A and experiences a supply shock, AND country A is not itself in crisis, A will increase its output with a probability equal to B's share of A's exports. Thus, the more important B is to A, the greater the likelihood that B will assist it. The production ramp-up is temporary, and will cease as soon as A ceases to experience a supply shock. Since oil is fungible, this temporary increase in production affects not only the country in supply shock, but all of A's trade partners. 
    * CONSUMPTION OF DOMESTIC PRODUCTION refers to the fraction of domestic consumption coming from domestic production -- in other words, the fraction of demand that is secure from external supply shocks. While treated as fixed in the model (at least from run to run), in reality this is a variable that many countries do have control over. In practice, this variable is primarily relevant to   countries where oil production is large relative to domestic demand. Note that making this tradeoff is not simple: it requires  long-term investment in refining and storage capabilities, and may involve giving up some of the financial and geopolitical benefits of oil exports in exchange for the increased insulation from external crises.

### OUT-OF-MODEL VARIABLES

As noted above, many variables that influence a country's overall energy security are not included in this model. Some of the major ones:

* NON-OIL ENERGY: Many countries meet large portions of their energy demands not only with oil but with natural gas, coal, and renewable sources. Diverse energy sources are likely to reduce the impact of oil supply shocks, while also introducing different risks and vulnerabilities. As the energy-security framing of the US debates on oil and gas extraction and alternative energies shows, countries may also actively seek to rebalance their mix of energy sources in order to mitigate risks. Thus, energy diversity is likely to decrease the overall energy risk estimated in this model.

* OIL TRANSPORTATION: This model does not incorporate the routes that oil takes from exporters to importers, and the chokepoints present on those routes. For example, crises in Iran or another Persian Gulf country risk leading to the disruption of the Straits of Hormuz, while crises in Egypt, Panama, or Malaysia pose risks to the chokepoints of the Suez Canal, Panama Canal and Straits of Malacca, respectively. This suggests that the model presented here will underestimate the systemic risks posed by crises in these regions.

* INTERVENTIONS: Major oil consumers in particular are likely to take action to mitigate many major risks to their oil imports, even far from their own borders. Perhaps the most striking example of this is the 1991 Gulf War, waged at least in part to avert a more serious crisis in Middle Eastern oil production. This is partially captured by the model, to the extent that it may randomly generate sets of crises corresponding to such scenarios, and that they are likely to be of short duration. However, the likelihood of intervention may be driven by a large variety of factors and diplomatic, military and geopolitical considerations wholly exogenous to the oil trade. Thus, it is difficult to assess whether the model is over- or under-estimating the risks by ignoring interventions. However, I argue that given this uncertaintly, the model generates a range of scenarios wide enough to implicitly incorporate the range of consequences of intervention and non-intervention.

* SYSTEMIC CHANGES: As described above, the worldwide architecture of the oil trade, the drivers of the model, is considered to be unchanging. However, countries can and do actively seek to change these drivers. These efforts range from changing the mix of trade partners to institution-building and mediation to minimize domestic and international risks. These efforts are often not unilateral, but take place in an environment of constant cooperation and competition. Nevertheless, these efforts take time to bear fruit and affect the system in meaningful ways: thus, they are less relevant to the short-term Long Present of this model.

### WILD CARDS

While most systemic changes are likely to take time to take effect, there are several 'wild card' scenarios which would likely rapidly reshape the system outside the envelope of the Long Present. These include:

* SIGNIFICANT GEOPOLITICAL REALIGNMENTS involving major oil producers and reshaping the trade network. Perhaps the most obvious example  would be events leading to the end of sanctions on Iran, which may lead to a rapid expansion of its oil industry coupled with increased trade with the West. Such a chance would likely have an effect on Iran's instability risk, and possibly that of other countries in the region as well.

* MAJOR CONFLICT, such as a war between the US and China, may substantially shift global demand, put major shipping routes at risk, and potentially change the trade network as producers came under pressure to align with one side or another.


## MODEL ANALYSIS

Each run of the model yields one simulated future; repeated runs yield a range of possible outcomes.

### GLOBAL OUTCOMES

I first characterize each run by the average and variance of its energy security measures. The mean indicates whether a realization is tends to feature greater supply or demand overall, while the variance is a measure of stability. These two measures together allow us to differentiate between scenarios where the demand/supply ratio is extreme (high or low) but stable, and those where the average may be close to 1 but the actual value swings wildly up and down unstably. 
The average ratios are distributed following a rough bell-curve shape that is slightly right-skewed, meaning that the model narrowly predicts a majority of scenarios characterized by supply insecurity. There is only weak correlation between the ratio means and variance, suggesting that high uncertainly and high overall insecurity do not necessarily occur together. Nevertheless, the most extreme scenarios all see high variance and mean ratios -- very high uncertainty does come hand in hand with high overall insecurity. Supply and Demand ratios follow similar patterns. 
Examining the traces of specific scenarios allows us to see that extreme swings are generally unstable -- ratios spike up or down, but quickly revert back towards the mean. I observe approximately 2% of scenarios where worldwide supply or demand deviations of over 50% that persist for over one year of the simulation. However, the histogram of all monthly ratios indicates that overall, the distribution is skewed towards supply crises, though is still roughly bell-shaped. 

### COUNTRY-SPECIFIC OUTCOMES

There is a wide distribution in the mean and variance of supply ratios of different countries across all the model realizations. Not surprisingly perhaps, the most vulnerable countries tend to be smaller and poorer, while the least vulnerable countries are more likely to be developed. There are some surprises: for example, Venezuela is an extremely high-risk country despite being a major oil producer. This may be due in part to missing data on Venezuela's consumption of its own production; however, it also highlights that domestic production is not itself a sufficient guarentee of energy security.
To a much greater extent than for the global-level analysis, country-specific variance correlates strongly and positively with mean supply ratios. This means that for individual countries,  uncertainty and insecurity go hand in hand.

Histograms of monthly ratios for specific countries across all scenarios help highlight and characterized the overall estimated risk. For example, the United States oil supply ratio is most likely to be close to 1 -- however, there is approximately an 8% probability of a serious supply crisis (ratio > 2) occuring at least once in the timeframe of the model, and a ~1% probability of a catastrophic crisis (ratio > 4). In comparsion, China has a slightly larger probability (10%) of smaller supply crises, but under no scenario experiences shocks as extreme as the US's most extreme ones.

# POLICY RECOMMENDATIONS

* The energy security situation of any specific country does not necessarily track with global energy security. 
* Diversity in both imports and exports is vital to energy security.
* Reserves and excess capacity help mitigate the risks of the majority of crises.
* Policymakers should be prepared for low-probability, high-impact events, particularly those arising from multiple simultaneous political crises.
* Computer simulation provides a method of extracting quantitative information from a qualitative understanding of the world, and can generate ranges of scenarios and future outcomes.







