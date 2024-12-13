; colon <key>, <link>
; db <value>

%define previous_element 0 	; изначально указатель списка должен быть 0
							; с каждым следующим использованием макроса
							; в previous_element будет хранится метка на предыдущий элемент

%macro colon 2
	%2: 					; здесь будет метка из 2-ого аргумента. Представляет собой ссылку на текущий элемент
	dq previous_element		; здесь будет метка на предыдущий элемент
	db %1, 0				; здесь будет "ключ" из 1-ого аргумента
							; значение указываем после макроса в words.inc
	
	%define previous_element %2	; обновляем указатель для того, 
								; чтобы следующий элемент указывал на текущий
%endmacro