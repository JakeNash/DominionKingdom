import boto3

def import_csv_to_dynamodb(table_name, csv_file_name, column_names, column_types):
    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table(table_name)
    
    csv_file = open(csv_file_name, 'r')
    with table.batch_writer() as batch:
        for cur_line in csv_file:
            cur_line = cur_line.strip().split(',')
            
            row = {}
            for column_number, column_name in enumerate(column_names):
                row[column_name] = cur_line[column_number]

            print(str(row['Name']))
            batch.put_item(
                Item={
                    'Box': row['Box'],
                    'Name': row['Name'],
                    'CardType': row['CardType'],
                    'Cost': row['Cost'],
                    'Pickable': row['Pickable'],
                    'Setup': row['Setup']
                }
            )

    csv_file.close()


def main():
    column_names = 'Box Name CardType Cost Pickable Setup'.split()
    table_name = 'DominionCards'
    csv_file_name = 'cards.csv'
    column_types = [str, str, int, bool, str, str]
    import_csv_to_dynamodb(table_name, csv_file_name, column_names, column_types)


if __name__ == "__main__":
    main()

