#!/bin/bash

function practice() {
	echo "$#"
}
practice "Hello, World!" "This is a practice function."

Fruits=('Apple' 'Banana' 'Orange')

for fruit in "${Fruits[@]}"; do
	echo "I like $fruit"
done

echo "${!Fruits[@]}"

dir=${0%/*}

echo $0