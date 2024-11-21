import re
import argparse

def process_file(input_filename, output_filename):
    result = {}

    with open(input_filename, 'r', encoding='utf-8') as file:
        for line in file:
            match = re.search(r'Unresolved reference: (\w+)', line)
            if match:
                key = match.group(1)
                value = re.sub(r'^Ease', 'ChatUIKit', key)
                result[key] = value


    with open(output_filename, 'w') as f:
        for key, value in result.items():
            f.write(f"\"{key}\": \"{value}\",\n")
            print(f"Wrote to file: {key}: {value}")

def main():
    parser = argparse.ArgumentParser(description='Process a file to replace keys and values.')
    parser.add_argument('input_file', help='The input file to process')
    parser.add_argument('-o', '--output_file', default='result.txt', help='The output file to write results to')

    args = parser.parse_args()

    process_file(args.input_file, args.output_file)

if __name__ == '__main__':
    main()
