import pandas
import json

def make_dataframes(path, country_data=True):
    '''
    Load a model output JSON and parse it into two Pandas DataFrame, one for 
    globals and another for country-level parameters.
    '''

    data = json.load(open(path))

    # Build the variable lists
    parameters = []
    global_series = []
    country_series = []
    for var in data[0]:
        if type(data[0][var]) is dict:
            country_series.append(var)
        elif type(data[0][var]) is list:
            global_series.append(var)
        else:
            parameters.append(var)

    # Build DataFrames
    global_rows = []
    country_rows = []
    if country_data:
        country_list = data[0][country_series[0]].keys()

    for n, iteration in enumerate(data):
        params = {p: iteration[p] for p in parameters}
        params["Iteration"] = n
        for i in range(60): # Hard-code number of ticks, for now
            # Build global row
            row = {"Tick": i}
            for key in global_series:
                row[key] = iteration[key][i]
            row.update(params)
            global_rows.append(row)
            
            if not country_data: continue

            # Build local rows
            for country in country_list:
                row = {"Tick": i, "Country": country}
                for key in country_series:
                    row[key] = iteration[key][country][i]
                row.update(params)
                country_rows.append(row)

    global_df = pandas.DataFrame(global_rows)
    if country_data:
        country_df = pandas.DataFrame(country_rows)
        return (global_df, country_df)
    else:
        return global_df


