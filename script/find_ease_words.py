import re
import argparse

def process_file(input_filename, output_filename):
    result = {}

    with open(input_filename, 'r', encoding='utf-8') as file:
        for line in file:
            matches = re.findall(r'\b(ease_\w+)\b', line)
            for match in matches:
                key = match
                value = key.replace('ease_', 'uikit_', 1)
                result[key] = value

    with open(output_filename, 'w', encoding='utf-8') as file:
        file.write("{\n")
        for key, value in result.items():
            file.write(f'    "{key}": "{value}",\n')
        file.write("}\n")

def main():
    parser = argparse.ArgumentParser(description='Process a file to replace keys and values.')
    parser.add_argument('input_file', help='The input file to process')
    parser.add_argument('-o', '--output_file', default='result.txt', help='The output file to write results to')

    args = parser.parse_args()

    process_file(args.input_file, args.output_file)

if __name__ == '__main__':
    main()
